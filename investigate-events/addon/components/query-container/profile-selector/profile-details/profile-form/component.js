import Component from '@ember/component';
import layout from './template';
import { hasUniqueName } from 'investigate-events/util/validations';

export default Component.extend({
  layout,
  classNames: ['profile-form'],
  classNameBindings: ['isNameError'],
  profile: null, // profile to create
  columnGroups: [], // list of column groups
  selectedColumnGroupId: null, // id of currently selected column group
  metaGroups: [], // list of meta groups,
  profiles: [], // list of profiles
  editProfile: () => {}, // list-manager function that accepts validated edited item
  isNameError: false,
  nameInvalidMessage: null,

  // profile parameters
  profileName: null,
  columnGroup: null,
  metaGroup: null,
  preQuery: null,

  didInsertElement() {
    // currently selected column group by default
    this.set('columnGroup', this.get('columnGroups')?.find(({ id }) => id === this.get('selectedColumnGroupId')));
    this.set('metaGroup', this.get('metaGroups')[0]); // TODO temporary default until we allow user selection
    this.set('preQuery', 'service=24,25,109,110,995,143,220,993'); // TODO temporary until prequery component is ready
  },

  _broadcastChangedProfile() {
    const { profileName, columnGroup, metaGroup, preQuery } = this.getProperties('profileName', 'columnGroup', 'metaGroup', 'preQuery');

    const newProfile = {};
    newProfile.name = profileName?.trim();
    newProfile.columnGroup = columnGroup,
    newProfile.metaGroup = metaGroup;
    newProfile.preQuery = preQuery;

    this.get('editProfile')(newProfile);
  },

  _validateForErrors(value) {
    const profiles = this.get('profiles') || [];

    const isNameError = !hasUniqueName(value, this.get('profile')?.id, profiles);
    const nameInvalidMessage = isNameError ? this.get('i18n').t('investigate.profile.profileNameNotUnique') : null;

    this.set('isNameError', isNameError);
    this.set('nameInvalidMessage', nameInvalidMessage);
  },

  actions: {
    handleNameChange(value) {
      this.set('profileName', value);
      this._validateForErrors(value);
      this._broadcastChangedProfile();
    },
    onColumnGroupChange(columnGroup) {
      this.set('columnGroup', columnGroup);
    }
  }
});
