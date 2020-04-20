import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['profile-view'],
  profile: null // profile to display
});
