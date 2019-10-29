import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

export default Component.extend({
  layout,
  classNames: ['profile-details'],
  profile: null, // profile to display
  editProfile: null, // list-manager function that accepts validated edited item
  pillsData: null, // pillsData to handle prequery pills change

  @computed('profile')
  isEditing(profile) {
    return _.isEmpty(profile) ? true : profile.isEditable;
  }
});
