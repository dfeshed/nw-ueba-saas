import Component from '@ember/component';

export default Component.extend({
  testId: 'endpointEventRow',
  attributeBindings: ['testId:test-id'],
  mainComponentClass: 'rsa-incident/events-list-row/endpoint/main',
  detailComponentClass: 'rsa-incident/events-list-row/endpoint/detail'
});
