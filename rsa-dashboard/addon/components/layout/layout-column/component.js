import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'box',

  classNames: ['layout-column'],

  classNameBindings: ['columnClass'],

  attributeBindings: ['columnClass:test-id'],

  columnClass: null
});
