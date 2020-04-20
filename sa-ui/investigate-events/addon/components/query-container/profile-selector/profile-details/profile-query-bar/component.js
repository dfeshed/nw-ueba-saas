import Component from '@ember/component';
import layout from './template';
import { inject } from '@ember/service';

export default Component.extend({
  layout,
  classNames: ['profile-query-bar'],
  tagName: 'vbox',
  i18n: inject()
});
