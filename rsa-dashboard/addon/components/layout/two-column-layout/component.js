import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  classNames: ['two-column-layout'],

  tagName: 'hbox',

  config: null
});
