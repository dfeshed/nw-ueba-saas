import Component from '@ember/component';
import layout from './template';

const ProfileSelector = Component.extend({
  layout,
  classNames: ['rsa-investigate-query-container__profile-selector'],
  modelName: null,
  listName: null,
  stateLocation: null,
  profiles: null, // list of profiles
  selectProfile: null, // TODO action
  helpId: null,
  actions: {
    setProfile(profile) {
      // TODO
      this.get('selectProfile')(profile);
    }
  }
});

export default ProfileSelector;
