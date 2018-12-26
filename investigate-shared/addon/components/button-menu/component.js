import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'ul',

  classNames: ['button-menu'],

  classNameBindings: ['isExpanded:expanded:collapsed', 'menuStyle'],

  attributeBindings: ['style'],

  style: null,

  isExpanded: false,

  menuStyle: null
});
