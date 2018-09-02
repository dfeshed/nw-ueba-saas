import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  testId: 'genericEventRow',
  attributeBindings: ['testId:test-id'],
  mainComponentClass: 'rsa-incident/events-list-row/generic/main',
  detailComponentClass: 'rsa-incident/events-list-row/generic/detail'
});
