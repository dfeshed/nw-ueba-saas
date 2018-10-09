import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  testId: 'endpointEventMain',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-info']
});
