import Component from '@ember/component';
import _ from 'lodash';

import layout from './template';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import { hasUniqueName } from 'investigate-events/util/validations';

export default Component.extend({
  layout,
  classNames: ['profile-form'],
  classNameBindings: ['isNameError'],

  // INPUTS

  // A profile contains a column group, this list
  // is used to select one
  columnGroups: [],

  // Callback that accepts validated edited item
  editProfile: () => {},

  // A profile contains a column group, this list
  // is used to select one, for now the first one
  // is automatically selected
  metaGroups: [],

  // A profile contains pills for a prequery, the
  // pills provided here will pre-populate the prequery
  pillsData: null,

  // Either an empty profile or the profile we want
  // to edit
  profile: null,

  // Existing list of profiles, used for doing name
  // uniqueness check
  profiles: [],

  // id of currently selected column group
  selectedColumnGroupId: null,

  // END INPUTS


  // INTERNAL STATE

  isNameError: false,
  nameInvalidMessage: null,

  // profile parameters
  name: null, // string
  columnGroup: null, // object
  columnGroupView: null, // string 'SUMMARY_VIEW' or 'CUSTOM'
  metaGroup: null, // object
  preQuery: null, // string

  // initialize form data when edit has begun
  // reset form data when profile is created, updated or reset
  didReceiveAttrs() {
    this.updateFormData();
  },

  // initialize a working copy of original profile
  updateFormData() {
    // reset error state
    if (this.get('isNameError')) {
      this.set('isNameError', false);
      this.set('nameInvalidMessage', null);
    }

    const profile = this.get('profile');

    // use this boolean to decide whether or not to update columnGroup in form
    // to not overwrite already selected column group in form
    const formHasColumnGroup = !!this.get('columnGroup');
    let newColumnGroup;

    // If profile exists, then we are editing
    // an existing profile, otherwise we are
    // creating a new one
    if (profile) {
      newColumnGroup = formHasColumnGroup ? undefined : this.get('columnGroups')?.find(({ id }) => id === profile.columnGroup.id);
      this.set('name', profile.name);
      this.set('columnGroupView', profile.columnGroupView);
      this.set('metaGroup', profile.metaGroup);
      this.set('preQuery', profile.preQuery);

    } else {
      // The first meta groups is used as a temporary default
      // until we allow user selection
      // TODO replace this when meta groups are introduced
      newColumnGroup = formHasColumnGroup ? undefined : this.get('columnGroups')?.find(({ id }) => id === this.get('selectedColumnGroupId'));
      this.set('metaGroup', this.get('metaGroups')[0]);
    }

    // if columnGroup needs to be updated
    if (newColumnGroup) {
      this.set('columnGroup', newColumnGroup);
    }

    // For list-manager to light up the "Save" button
    // we need to broadcast the edit data so that
    // list-manager knows something has "changed"
    //
    // TODO: Why do we have to broadcast for "new" as
    // well?
    this._broadcastChangedProfile();
  },

  // Pull all the form fields, assemble a new profile
  // object and provide it to list manager callback
  // which allows buttons to shift/change
  _broadcastChangedProfile() {
    const { name, columnGroup, metaGroup } =
      this.getProperties('name', 'columnGroup', 'metaGroup');
    const newProfile = _.cloneDeep(this.get('profile')) || {};
    newProfile.name = name?.trim();
    newProfile.columnGroup = columnGroup;
    newProfile.columnGroupView = columnGroup?.id === 'SUMMARY' ? 'SUMMARY_VIEW' : 'CUSTOM';
    newProfile.metaGroup = metaGroup;
    let newPreQuery;

    // pillsData is updated in state and finds its way
    // back to this component from above
    if (this.get('pillsData')) {
      newPreQuery = encodeMetaFilterConditions(this.get('pillsData'));
    }
    newProfile.preQuery = newPreQuery;

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
    onNameChange(value) {
      this.set('name', value);
      this._validateForErrors(value);
      this._broadcastChangedProfile();
    },

    onColumnGroupChange(columnGroup) {
      this.set('columnGroup', columnGroup);
      this._broadcastChangedProfile();
    }
  }
});
