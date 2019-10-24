import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

export default Component.extend({
  layout,
  classNames: ['profile-details'],
  profile: null, // profile to display
  editProfile: null, // list-manager function that accepts validated edited item

  @computed('profile')
  isEditing(profile) {
    if (!_.isEmpty(profile)) {
      // TODO Edit Group Items. Until then, custom profile will also be read-only
      // return profile.isEditable;
      return false;
    }
    return true;
  }
});
