import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['top-alert-item'],

  attributeBindings: ['testId:test-id'],

  testId: 'top-item'
});
