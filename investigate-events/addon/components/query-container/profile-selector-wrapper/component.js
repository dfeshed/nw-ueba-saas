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
import { enrichedProfiles } from 'investigate-events/reducers/investigate/profile/selectors';

const dispatchToActions = {
  // TODO setProfile
};

const stateToComputed = (state) => ({
  profiles: enrichedProfiles(state)
});

const ProfileSelectorWrapper = Component.extend({
  layout,
  classNames: ['profile-selector-wrapper'],
  // TODO eventBus: service(),
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
    // TODO
    selectProfile() {
      // this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
      // this.send('setProfile', profile);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ProfileSelectorWrapper);
