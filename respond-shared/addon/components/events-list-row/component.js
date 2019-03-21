import layout from './template';
import { next, join } from '@ember/runloop';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { guidFor } from '@ember/object/internals';
import HighlightsEntities from 'context/mixins/highlights-entities';
import { createProcessAnalysisLink } from 'respond-shared/utils/event-analysis';

const GENERIC = 'events-list-row/generic';
const ENDPOINT = 'events-list-row/endpoint';
const UEBA = 'events-list-row/ueba';
const PROCESS = 'events-list-row/ueba/process';

export default Component.extend(HighlightsEntities, {
  layout,
  tagName: 'li',
  entityEndpointId: 'IM',
  autoHighlightEntities: true,
  testId: 'eventsListRow',
  classNameBindings: ['expanded'],
  classNames: ['events-list-table-row'],
  attributeBindings: ['testId:test-id'],

  didUpdateAttrs() {
    this._super(...arguments);
    join(() => {
      this.teardownEntities();
      next(this, 'highlightEntities');
    });
  },

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
        type: 'analyze_process',
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

  @computed('item.device_type', 'item.schema')
  componentClasses(deviceType, schema) {
    let componentClass;
    switch (schema || deviceType) {
      case 'FILE':
      case 'AUTHENTICATION':
      case 'ACTIVE_DIRECTORY':
      case 'REGISTRY':
        componentClass = UEBA;
        break;
      case 'PROCESS':
        componentClass = PROCESS;
        break;
      case 'nwendpoint':
        componentClass = ENDPOINT;
        break;
      default:
        componentClass = GENERIC;
    }
    return {
      header: `${componentClass}/header`,
      footer: `${componentClass}/footer`,
      detail: `${componentClass}/detail`
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
