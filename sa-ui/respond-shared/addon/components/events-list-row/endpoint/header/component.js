import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'endpointEventHeader',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-endpoint-header']
});
