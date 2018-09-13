import Component from '@ember/component';

export default Component.extend({
  testId: 'genericEventRow',
  attributeBindings: ['testId:test-id'],
  mainComponentClass: 'rsa-incident/events-list-row/generic/main',
  detailComponentClass: 'rsa-incident/events-list-row/generic/detail'
});
