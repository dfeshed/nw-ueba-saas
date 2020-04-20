import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'ul',
  classNames: ['rsa-dropdown-action-list'],

  list: null

});
