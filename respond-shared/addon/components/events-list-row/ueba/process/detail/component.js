import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  entityEndpointId: 'IM',
  autoHighlightEntities: true,
  testId: 'uebaProcessEventDetail',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-process-detail']
});
