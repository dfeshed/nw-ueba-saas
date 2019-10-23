import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import layout from './template';
import {
  PROFILES_STATE_LOCATION as stateLocation,
  PROFILES_MODEL_NAME as modelName,
  PROFILES_LIST_NAME as listName,
  PROFILES_TOPIC_ID
} from 'investigate-shared/constants/profiles';
import { enrichedProfiles, enrichedProfile, languageAndAliases } from 'investigate-events/reducers/investigate/profile/selectors';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { metaGroups } from 'investigate-events/reducers/investigate/meta-group/selectors';
import { selectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { setProfile } from 'investigate-events/actions/interaction-creators';

const dispatchToActions = {
  setProfile
};

const stateToComputed = (state) => ({
  profiles: enrichedProfiles(state),
  languageAndAliases: languageAndAliases(state),
  columnGroups: columnGroups(state),
  metaGroups: metaGroups(state),
  selectedColumnGroupId: selectedColumnGroup(state)
});

const ProfileSelectorWrapper = Component.extend({
  layout,
  classNames: ['profile-selector-wrapper'],

  modelName,
  listName,
  stateLocation,

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
      return enrichedProfile(profile, this.get('languageAndAliases'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ProfileSelectorWrapper);
