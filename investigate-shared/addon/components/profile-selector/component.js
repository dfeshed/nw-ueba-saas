import Component from '@ember/component';
import layout from './template';

const ProfileSelector = Component.extend({
  layout,
  classNames: ['rsa-investigate-query-container__profile-selector'],
  modelName: null,
  listName: null,
  stateLocation: null,
  profiles: null, // list of profiles
  metaGroups: null, // list of meta groups
  columnGroups: null, // list of column groups
  selectedColumnGroupId: null, // id of currently selected column group
  selectProfile: null, // function
  enrichProfile: null, // function to use for itemTransform in list manager details
  helpId: null,
  actions: {
    setProfile(profile) {
      this.get('selectProfile')(profile);
    }
  }
});

export default ProfileSelector;
