import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { guidFor } from '@ember/object/internals';

const GENERIC = 'rsa-incident/events-list-row/generic';
const ENDPOINT = 'rsa-incident/events-list-row/endpoint';

export default Component.extend({
  tagName: 'li',
  testId: 'eventsListRow',
  classNames: ['events-list-table-row'],
  attributeBindings: ['testId:test-id'],

  @computed('item.id')
  eventIndex(itemId) {
    const id = itemId && itemId.toString() || '';
    const tokens = id.match(/(.*)\:(.*)/) || [];
    const index = tokens.length === 3 ? tokens[2] : '0';
    return parseInt(index, 10) + 1;
  },

  @computed('alerts', 'item.indicatorId')
  associatedAlert(alerts, indicatorId) {
    return alerts && alerts.find((alert) => alert.indicatorId === indicatorId);
  },

  @computed('item.device_type')
  componentClass(deviceType) {
    return deviceType === 'nwendpoint' ? ENDPOINT : GENERIC;
  },

  @computed('componentClass')
  detailComponentClass(componentClass) {
    return `${componentClass}/detail`;
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
  ariaExpanded(expandedId, itemId) {
    return expandedId === itemId;
  },

  @computed('ariaExpanded')
  collapsed(ariaExpanded) {
    return !ariaExpanded;
  },

  actions: {
    showDetail() {
      this.expand(this.item.id);
    }
  }

});
