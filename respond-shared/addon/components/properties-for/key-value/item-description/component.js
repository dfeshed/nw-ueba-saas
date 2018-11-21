import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'dd',
  testId: 'keyValue',
  classNames: ['key-value'],
  attributeBindings: ['testId:test-id']
});
