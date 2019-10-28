import Component from '@ember/component';
import layout from './template';
import _ from 'lodash';

export default Component.extend({
  layout,
  classNames: ['profile-view'],
  profile: null, // profile to display

  didReceiveAttrs() {
    // profile that is created renders as a read-only profile until edit profiles is implemented
    // this results in validation of the profiles that was just created to activate the correct footer buttons
    // should be moved back to profile-form once custom profiles are editable
    this.editProfile(_.cloneDeep(this.get('profile')));
  }
});
