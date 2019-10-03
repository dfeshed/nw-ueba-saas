import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['profile-details'],
  profile: null // profile to display
});
