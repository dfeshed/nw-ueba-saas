import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'propertiesFor',
  attributeBindings: ['testId:test-id'],
  classNames: ['properties-for']
});
