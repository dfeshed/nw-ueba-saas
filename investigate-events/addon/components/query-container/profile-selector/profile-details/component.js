import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

import layout from './template';

export default Component.extend({
  layout,
  classNames: ['profile-details'],

  profile: null, // profile to display
  editProfile: null, // list-manager function that accepts validated edited item
  pillsData: null, // pillsData to handle prequery pills change
  columnGroups: null, // column groups

  @computed('profile')
  isEditing(profile) {
    return _.isEmpty(profile) ? true : profile.isEditable;
  }
});
