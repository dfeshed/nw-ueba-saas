import { computed } from '@ember/object';
import layout from './template';
import { next, join } from '@ember/runloop';
import Component from '@ember/component';
import { guidFor } from '@ember/object/internals';
import HighlightsEntities from 'context/mixins/highlights-entities';
import { createProcessAnalysisLink, createEventAnalysisLink } from 'respond-shared/utils/event-analysis';
import { inject as service } from '@ember/service';
import _ from 'lodash';

const GENERIC = 'events-list-row/generic';
const ENDPOINT = 'events-list-row/endpoint';
const UEBA = 'events-list-row/ueba';
const PROCESS = 'events-list-row/ueba/process';
const TLS = 'events-list-row/ueba/tls';

export default Component.extend(HighlightsEntities, {
  layout,
  tagName: 'li',
  entityEndpointId: 'IM',
  autoHighlightEntities: true,
  testId: 'eventsListRow',
  classNameBindings: ['expanded'],
  classNames: ['events-list-table-row'],
  attributeBindings: ['testId:test-id'],

  investigatePage: service(),

  didUpdateAttrs() {
    this._super(...arguments);
    join(() => {
      this.teardownEntities();
      next(this, 'highlightEntities');
    });
  },

  tabIndex: computed('expanded', function() {
    return this.expanded ? '0' : '-1';
  }),

  eventIndex: computed('item.eventIndex', function() {
    return this.item?.eventIndex && parseInt(this.item?.eventIndex, 10) + 1 || 1;
  }),

  customizedItem: computed('item', 'services', 'investigatePage.legacyEventsEnabled', function() {
    const modifiedItem = _.cloneDeep(this.item);
    // eslint-disable-next-line camelcase
    if (modifiedItem?.related_links) {
      // Check if investigate original event specific related link exists in given event
      const isInvestigateEvent = modifiedItem.related_links.some((e) => e.type === 'investigate_original_event');
      // Create event analysis url and replaced it with legacy events url for investigate event if legacy events flag is disabled
      if (isInvestigateEvent && !this.investigatePage?.legacyEventsEnabled) {
        modifiedItem.related_links[0].url = createEventAnalysisLink(this.item, this.services);
      }

      const processAnalysisLink = createProcessAnalysisLink(this.item, this.services);
      if (processAnalysisLink) {
        modifiedItem.related_links.push({
          type: 'analyze_process',
          url: processAnalysisLink
        });
      }
    }
    return modifiedItem;
  }),

  associatedAlert: computed('alerts', 'item.indicatorId', function() {
    return this.alerts && this.alerts.find((alert) => alert.indicatorId === this.item?.indicatorId);
  }),

  componentClasses: computed('item.device_type', 'item.schema', function() {
    let componentClass;
    // eslint-disable-next-line camelcase
    switch (this.item?.schema || this.item?.device_type) {
      case 'FILE':
      case 'AUTHENTICATION':
      case 'ACTIVE_DIRECTORY':
      case 'REGISTRY':
        componentClass = UEBA;
        break;
      case 'TLS':
        componentClass = TLS;
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

  }),

  guid: computed(function() {
    return guidFor(this);
  }),

  ariaControls: computed('guid', function() {
    return `${this.guid}-row-details`;
  }),

  expanded: computed('expandedId', 'item.id', function() {
    return this.expandedId === this.item?.id;
  }),

  collapsed: computed('expanded', function() {
    return !this.expanded;
  }),

  actions: {
    showDetail() {
      this.expand(this.item.id);
    }
  }

});
