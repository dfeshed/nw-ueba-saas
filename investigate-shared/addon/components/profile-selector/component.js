import Component from '@ember/component';
import layout from './template';

const ProfileSelector = Component.extend({
  layout,
  classNames: ['rsa-investigate-query-container__profile-selector'],
  modelName: null,
  listName: null,
  stateLocation: null,
  profiles: null, // list of profiles
  selectProfile: null,
  helpId: null,
  actions: {
    setProfile(profile) {
      this.get('selectProfile')(profile);
    }
  }
});

export default ProfileSelector;
