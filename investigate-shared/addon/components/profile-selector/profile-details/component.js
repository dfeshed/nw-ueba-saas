import Component from '@ember/component';
import layout from './template';
import { inject } from '@ember/service';

export default Component.extend({
  layout,
  classNames: ['profile-details'],
  i18n: inject(),
  profile: null // profile to display
});
