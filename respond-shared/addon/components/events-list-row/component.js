import layout from './template';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { guidFor } from '@ember/object/internals';
import HighlightsEntities from 'context/mixins/highlights-entities';
import { createProcessAnalysisLink } from 'respond-shared/utils/event-analysis';

const GENERIC = 'events-list-row/generic';
const ENDPOINT = 'events-list-row/endpoint';

export default Component.extend(HighlightsEntities, {
  layout,
  tagName: 'li',
  entityEndpointId: 'IM',
  autoHighlightEntities: true,
  testId: 'eventsListRow',
  classNameBindings: ['expanded'],
  classNames: ['events-list-table-row'],
  attributeBindings: ['testId:test-id'],

  @computed('expanded')
  tabIndex(expanded) {
    return expanded ? '0' : '-1';
  },

  @computed('item.eventIndex')
  eventIndex(index) {
    return index && parseInt(index, 10) + 1 || 1;
  },

  @computed('item', 'services')
  customizedItem(item, services) {
    const processAnalysisLink = createProcessAnalysisLink(item, services);

    if (processAnalysisLink) {
      const modifiedItem = { ...item };

      modifiedItem.related_links = modifiedItem.related_links.concat({
        type: 'process_analysis',
        url: processAnalysisLink
      });
      return modifiedItem;
    }
    return item;
  },

  @computed('alerts', 'item.indicatorId')
  associatedAlert(alerts, indicatorId) {
    return alerts && alerts.find((alert) => alert.indicatorId === indicatorId);
  },

  @computed('item.device_type')
  componentClasses(deviceType) {
    const componentClass = deviceType === 'nwendpoint' ? ENDPOINT : GENERIC;
    const header = `${componentClass}/header`;
    const footer = `${componentClass}/footer`;
    const detail = `${componentClass}/detail`;
    return {
      header,
      footer,
      detail
    };
  },

  @computed()
  guid() {
    return guidFor(this);
  },

  @computed('guid')
  ariaControls(guid) {
    return `${guid}-row-details`;
  },

  @computed('expandedId', 'item.id')
  expanded(expandedId, itemId) {
    return expandedId === itemId;
  },

  @computed('expanded')
  collapsed(expanded) {
    return !expanded;
  },

  actions: {
    showDetail() {
      this.expand(this.item.id);
    }
  }

});
