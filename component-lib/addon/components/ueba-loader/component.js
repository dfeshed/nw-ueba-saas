import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  testId: 'uebaLoader',
  attributeBindings: ['testId:test-id'],
  classNames: ['ueba-loading__main']
});
