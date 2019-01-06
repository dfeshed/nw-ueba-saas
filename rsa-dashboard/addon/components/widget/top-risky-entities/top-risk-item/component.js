import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['top-risk-item'],

  attributeBindings: ['testId:test-id'],

  testId: 'top-risk-item'
});
