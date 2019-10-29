import Component from '@ember/component';
import _ from 'lodash';
import layout from './template';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
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
  pillsData: null, // for prequery pills change
  editProfile: () => {}, // list-manager function that accepts validated edited item
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
    this.initializeFormData();
  },

  /**
   * initialize a working copy of original profile
   */
  initializeFormData() {
    if (this.get('isNameError')) {
      this.set('isNameError', false);
      this.set('nameInvalidMessage', null);
    }

    const profile = this.get('profile');
    if (profile) { // editing an existing profile
      this.set('name', profile.name);
      const columnGroupFound = this.get('columnGroups')?.find(({ id }) => id === profile.columnGroup.id);
      this.set('columnGroup', columnGroupFound);
      this.set('columnGroupView', profile.columnGroupView);
      this.set('metaGroup', profile.metaGroup);
      this.set('preQuery', profile.preQuery);

    } else { // creating new profile
      this.set('columnGroup', this.get('columnGroups')?.find(({ id }) => id === this.get('selectedColumnGroupId')));
      this.set('metaGroup', this.get('metaGroups')[0]); // TODO temporary default until we allow user selection
    }

    this._broadcastChangedProfile();
  },

  _broadcastChangedProfile() {
    const { name, columnGroup, metaGroup } = this.getProperties('name', 'columnGroup', 'metaGroup');
    const newProfile = _.cloneDeep(this.get('profile')) || {};
    newProfile.name = name?.trim();
    newProfile.columnGroup = columnGroup;
    newProfile.columnGroupView = columnGroup?.id === 'SUMMARY' ? 'SUMMARY_VIEW' : 'CUSTOM';
    newProfile.metaGroup = metaGroup;
    // convert pillsData into preQuery string
    newProfile.preQuery = this.get('pillsData') ? encodeMetaFilterConditions(this.get('pillsData')) : undefined;

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
