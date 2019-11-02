import Component from '@ember/component';
import _ from 'lodash';

import layout from './template';
import { hasUniqueName } from 'investigate-events/util/validations';
import {
  COLUMN_GROUP_ID_SUMMARY,
  COLUMN_GROUP_VIEW_SUMMARY,
  COLUMN_GROUP_VIEW_CUSTOM
} from 'investigate-events/constants/columnGroups';

export default Component.extend({
  layout,
  classNames: ['profile-form'],
  classNameBindings: ['isNameError'],

  // INPUTS

  // A profile contains a column group, this list
  // is used to select one
  columnGroups: [],

  // A profile contains a column group, this list
  // is used to select one, for now the first one
  // is automatically selected
  metaGroups: [],

  // Either an empty profile or the profile we want
  // to edit
  profile: null,

  // Existing list of profiles, used for doing name
  // uniqueness check
  profiles: [],

  // id of currently selected column group
  selectedColumnGroupId: null,

  // action in profile-details component
  // send new profile object and trigger broadcast
  sendToBroadcast: () => {},

  // END INPUTS


  // INTERNAL STATE

  isNameError: false,
  nameInvalidMessage: null,

  // profile parameters
  name: null, // string
  columnGroup: null, // object
  columnGroupView: null, // string 'SUMMARY_VIEW' or 'CUSTOM'
  metaGroup: null, // object

  // set up or reset form
  didReceiveAttrs() {
    this._updateFormData();
  },

  _updateFormData() {
    this._resetErrorState();
    const profile = this.get('profile');
    if (!profile) {
      this._initializeNewFormData();
    } else {
      this._initializeEditFormData();
    }
  },

  _initializeNewFormData() {
    // The first meta groups is used as a temporary default
    // until we allow user selection
    // TODO replace this when meta groups are introduced
    const newColumnGroup = this.get('columnGroups') ?.find(({ id }) => id === this.get('selectedColumnGroupId'));
    this.set('metaGroup', this.get('metaGroups')[0]);
    this.set('columnGroup', newColumnGroup);
  },

  // called once when editing existing profile
  _initializeEditFormData() {
    const profile = this.get('profile');
    // set values based on original profile before edit begins
    this.set('name', profile.name);
    this.set('columnGroup', this.get('columnGroups')?.find(({ id }) => id === profile.columnGroup.id));
    this.set('columnGroupView', profile.columnGroupView);
    this.set('metaGroup', profile.metaGroup);
    this._broadcast();
  },

  _resetErrorState() {
    if (this.get('isNameError')) {
      this.set('isNameError', false);
      this.set('nameInvalidMessage', null);
    }
  },

  // called when a change happens in form
  _broadcast() {
    // For list-manager details-footer to know
    // which buttons to display and enable or disable
    // for example, to light up the "Save" button
    // or show Reset button or Close button
    // we need to broadcast the edit data so that
    // list-manager knows something has "changed"
    this.get('sendToBroadcast')(this._assembleNewProfileWithoutPreQuery());
  },

  // returns a profile object
  // assembled from component properties from form
  _assembleNewProfileWithoutPreQuery() {
    // create and populate newProfile object
    const newProfile = _.cloneDeep(this.get('profile')) || {};
    const { name, columnGroup, metaGroup } = this.getProperties('name', 'columnGroup', 'metaGroup');
    newProfile.name = name?.trim();
    newProfile.columnGroup = columnGroup;
    newProfile.columnGroupView = columnGroup?.id === COLUMN_GROUP_ID_SUMMARY ? COLUMN_GROUP_VIEW_SUMMARY : COLUMN_GROUP_VIEW_CUSTOM;
    newProfile.metaGroup = metaGroup;

    return newProfile;
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
      this._broadcast();
    },

    onColumnGroupChange(columnGroup) {
      this.set('columnGroup', columnGroup);
      this._broadcast();
    }
  }
});
