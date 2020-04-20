import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  classNames: ['tool-tip-value'],

  text: null,

  showNote: false
});
