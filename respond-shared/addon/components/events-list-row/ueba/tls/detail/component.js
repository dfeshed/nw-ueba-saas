import layout from './template';
import Component from '@ember/component';
import HighlightsEntities from 'context/mixins/highlights-entities';

export default Component.extend(HighlightsEntities, {
  layout,
  entityEndpointId: 'IM',
  autoHighlightEntities: true,
  testId: 'uebaTlsEventDetail',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-tls-detail']
});
