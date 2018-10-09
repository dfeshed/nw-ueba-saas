import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  testId: 'endpointEventDetail',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-endpoint-detail']
});
