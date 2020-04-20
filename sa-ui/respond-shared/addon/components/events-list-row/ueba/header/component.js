import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'uebaEventHeader',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-ueba-header']
});
