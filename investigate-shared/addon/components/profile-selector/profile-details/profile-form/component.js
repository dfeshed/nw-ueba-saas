import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['profile-form'],
  classNameBindings: ['isNameError'],
  profile: null, // profile to create
  columnGroups: [], // list of column groups
  selectedColumnGroupId: null, // id of currently selected column group
  metaGroups: [], // list of meta groups,
  profiles: [], // list of profiles
  newProfile: null, // new profile on create or edit
  editProfile: () => {}, // list-manager function that accepts validated edited item
  isNameError: false,
  nameInvalidMessage: null,

  didInsertElement() {
    this.set('newProfile', {
      // SUMMARY column group selected by default
      columnGroup: this.get('columnGroups')?.find(({ id }) => id === this.get('selectedColumnGroupId')),
      metaGroup: this.get('metaGroups')[0], // TODO temporary default until we allow user selection
      preQuery: 'service=24,25,109,110,995,143,220,993' // TODO temporary until prequery component is ready
    });
  },

  _checkDirtyChange() {
    let editedProfile = null;
    const newProfile = this.get('newProfile');

    // check for required fields
    if (!this.get('isNameError') && newProfile?.name && newProfile?.columnGroup &&
      newProfile?.metaGroup && newProfile?.preQuery) {

      const newProfileIsDirty = true; // TODO need check here for edit
      editedProfile = newProfileIsDirty ? newProfile : null;
    }

    // calling editProfile with null is an indicator to the called function that
    // the data being edited is currently invalid
    this.get('editProfile')(editedProfile);
  },

  _validateForErrors(value) {
    const profiles = this.get('profiles') || [];
    // TODO edit unique name check for new items only
    const nameIsUnique = !profiles.find((item) => item.name == value);
    const isNameError = !nameIsUnique;
    const nameInvalidMessage = isNameError ? this.get('i18n').t('investigate.profile.profileNameNotUnique') : null;

    this.set('isNameError', isNameError);
    this.set('nameInvalidMessage', nameInvalidMessage);
  },

  actions: {
    handleNameChange(value) {
      value = value.trim();
      const newProfile = this.get('newProfile');
      newProfile.name = value;
      this._validateForErrors(value);
      this._checkDirtyChange();
    },
    onColumnGroupChange(columnGroup) {
      this.set('newProfile.columnGroup', columnGroup);
    }
  }
});
