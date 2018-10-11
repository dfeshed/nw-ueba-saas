import layout from './template';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { guidFor } from '@ember/object/internals';

const GENERIC = 'events-list-row/generic';
const ENDPOINT = 'events-list-row/endpoint';

export default Component.extend({
  layout,
  tagName: 'li',
  testId: 'eventsListRow',
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

  @computed('alerts', 'item.indicatorId')
  associatedAlert(alerts, indicatorId) {
    return alerts && alerts.find((alert) => alert.indicatorId === indicatorId);
  },

  @computed('item.device_type')
  componentClass(deviceType) {
    return deviceType === 'nwendpoint' ? ENDPOINT : GENERIC;
  },

  @computed('componentClass')
  headerComponentClass(componentClass) {
    return `${componentClass}/header`;
  },

  @computed('componentClass')
  footerComponentClass(componentClass) {
    return `${componentClass}/footer`;
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
