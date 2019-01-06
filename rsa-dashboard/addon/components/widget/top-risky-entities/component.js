import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'vbox',

  classNames: ['top-risk-entity'],

  config: null // Dashlet configuration
});
