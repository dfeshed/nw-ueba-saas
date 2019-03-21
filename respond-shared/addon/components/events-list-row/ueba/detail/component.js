import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  entityEndpointId: 'IM',
  autoHighlightEntities: true,
  testId: 'uebaEventDetail',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-ueba-detail']
});
