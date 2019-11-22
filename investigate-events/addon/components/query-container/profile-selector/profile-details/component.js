import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import layout from './template';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import { CONTENT_TYPE_PUBLIC } from 'investigate-events/constants/profiles';

export default Component.extend({
  layout,
  classNames: ['profile-details'],

  // INPUTS
  profile: null, // profile to display

  // Callback that accepts validated edited item
  editProfile: () => {},

  // A profile contains pills for a prequery, the
  // pills provided here will pre-populate the prequery
  pillsData: null,

  columnGroups: null, // column groups
  // END OF INPUTS

  // INTERNAL
  // profile parameters sent up from profile-form
  name: null, // string
  columnGroup: null, // object
  columnGroupView: null, // string 'SUMMARY_VIEW' or 'CUSTOM'
  metaGroup: null, // object
  // END OF INTERNAL


  didReceiveAttrs() {
    // detect pillsData changes here and
    // broadcast profile
    this.send('broadcastChangedProfile');
  },

  @computed('profile')
  isEditing(profile) {
    return _.isEmpty(profile) ? true : profile.isEditable;
  },

  _getPreQueryString() {
    let newPreQuery;

    // pillsData is updated in state and finds its way
    // back to this component from above
    if (this.get('pillsData')) {
      newPreQuery = encodeMetaFilterConditions(this.get('pillsData'));
    }
    return newPreQuery;
  },

  // returns a profile object
  // assembled from component properties from form
  _assembleNewProfile() {
    // create and populate newProfile object
    const newProfile = _.cloneDeep(this.get('profile')) || {};
    const { name, columnGroup, columnGroupView, metaGroup } = this.getProperties('name', 'columnGroup', 'columnGroupView', 'metaGroup');
    newProfile.name = name?.trim();
    newProfile.columnGroup = columnGroup;
    newProfile.columnGroupView = columnGroupView;
    newProfile.metaGroup = metaGroup;
    newProfile.preQuery = this._getPreQueryString();

    // if profile is missing contentType property, set it to PUBLIC
    if (!newProfile.hasOwnProperty('contentType')) {
      newProfile.contentType = CONTENT_TYPE_PUBLIC;
    }

    return newProfile;
  },

  actions: {
    /**
     * (optional) receive new profile object from profile-form
     * to add pillsData and broadcast
     *
     * to send updated profile to list manager callback
     * for details-footer component to decide
     * which buttons to show
     * like Reset, Close, Save
     */
    broadcastChangedProfile(newProfile) {
      // if newProfile was passed up from profile-form component
      // then keep a copy of those properties in this component
      // for didReceiveAttrs to use to trigger broadcast
      // when pillsData changes
      if (newProfile) {
        this.set('name', newProfile.name);
        this.set('metaGroup', newProfile.metaGroup);
        this.set('columnGroup', newProfile.columnGroup);
        this.set('columnGroupView', newProfile.columnGroupView);
      }
      // broadcast
      this.get('editProfile')(this._assembleNewProfile());
    }
  }
});
