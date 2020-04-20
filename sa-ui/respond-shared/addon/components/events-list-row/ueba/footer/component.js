import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  testId: 'uebaEventFooter',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-ueba-footer']
});
