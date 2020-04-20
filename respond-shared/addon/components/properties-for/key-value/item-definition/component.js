import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'dt',
  testId: 'keyName',
  classNames: ['key-name'],
  attributeBindings: ['testId:test-id']
});
