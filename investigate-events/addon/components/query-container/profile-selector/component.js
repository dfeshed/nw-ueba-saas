import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import computed, { not } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import {
  PROFILES_STATE_LOCATION as stateLocation,
  PROFILES_MODEL_NAME as modelName,
  PROFILES_LIST_NAME as listName,
  PROFILES_TOPIC_ID
} from 'investigate-events/constants/profiles';
import {
  enrichedProfiles,
  enrichedProfile,
  languageAndAliases
} from 'investigate-events/reducers/investigate/profile/selectors';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { metaGroups } from 'investigate-events/reducers/investigate/meta-group/selectors';
import { selectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { pillsData } from 'investigate-events/reducers/investigate/query-node/selectors';
import { setProfile } from 'investigate-events/actions/interaction-creators';
import { isProfileValid } from 'investigate-events/util/validations';

const dispatchToActions = {
  setProfile
};

const stateToComputed = (state) => ({
  columnGroups: columnGroups(state),
  metaGroups: metaGroups(state),
  languageAndAliases: languageAndAliases(state),
  profiles: enrichedProfiles(state),
  selectedColumnGroupId: selectedColumnGroup(state),
  pillsData: pillsData(state)
});

const ProfileSelector = Component.extend({
  layout,
  classNames: ['rsa-investigate-query-container__profile-selector'],
  modelName,
  listName,
  stateLocation,
  selectProfile: null, // function
  enrichProfile: null, // function to use for itemTransform in list manager details
  accessControl: service(),
  @not('accessControl.hasInvestigateProfilesAccess') isDisabled: false,

  @computed('profiles')
  renderProfiles(profiles) {
    return !!profiles;
  },

  @computed()
  helpId() {
    return {
      moduleId: 'investigation',
      topicId: PROFILES_TOPIC_ID
    };
  },

  actions: {
    selectProfile(profile) {
      this.send('setProfile', profile, this.get('executeQuery'));
    },

    /**
     * enrich a profile with isEditable and preQueryPillsData
     * @param {object} profile
     */
    enrichProfile(profile) {
      return enrichedProfile(profile, this.get('languageAndAliases'), this.get('columnGroups'));
    },

    validateEditedProfile(editedProfile) {
      const profiles = this.get('profiles') || [];
      return isProfileValid(editedProfile, profiles);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ProfileSelector);
