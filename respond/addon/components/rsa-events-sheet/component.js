import Component from 'ember-component';
import CanThrottleAttr from 'respond/mixins/can-throttle-attr';

export default Component.extend(CanThrottleAttr, {
  tagName: '',

  // Configuration settings for throttling property values from "items" to "itemsThrottled".
  // @see respond/mixins/can-throttle-attrs
  throttleFromAttr: 'items',
  throttleToAttr: 'itemsThrottled',
  throttleInterval: 1000,

  totalCount: 0,

  columnsConfig: [{
    field: 'timestamp',
    title: 'respond.eventsTable.time',
    width: 150,
    visible: true
  }, {
    field: 'type',
    title: 'respond.eventsTable.type',
    visible: true
  }, {
    field: 'source.device.ip_address',
    fieldSuffix: 'ip_address',
    title: 'respond.eventsTable.sourceIP',
    visible: true
  }, {
    field: 'source.device.dns_hostname',
    fieldSuffix: 'dns_hostname',
    title: 'respond.eventsTable.sourceHost',
    visible: false
  }, {
    field: 'source.device.mac_address',
    fieldSuffix: 'mac_address',
    title: 'respond.eventsTable.sourceMAC',
    visible: false
  }, {
    field: 'destination.device.ip_address',
    fieldSuffix: 'ip_address',
    title: 'respond.eventsTable.destinationIP',
    visible: true
  }, {
    field: 'destination.device.dns_hostname',
    fieldSuffix: 'dns_hostname',
    title: 'respond.eventsTable.destinationHost',
    visible: false
  }, {
    field: 'destination.device.mac_address',
    fieldSuffix: 'mac_address',
    title: 'respond.eventsTable.destinationMAC',
    visible: false
  }, {
    field: 'detector.ip_address',
    fieldSuffix: 'ip_address',
    title: 'respond.eventsTable.detectorIP',
    visible: true
  }, {
    field: 'source.user.username',
    fieldSuffix: 'username',
    title: 'respond.eventsTable.sourceUser',
    visible: true
  }, {
    field: 'destination.user.username',
    fieldSuffix: 'username',
    title: 'respond.eventsTable.destinationUser',
    visible: true
  }, {
    field: 'data.0.filename',
    fieldSuffix: 'filename',
    title: 'respond.eventsTable.fileName',
    visible: true
  }, {
    field: 'data.0.hash',
    fieldSuffix: 'hash',
    title: 'respond.eventsTable.fileHash',
    width: 150,
    visible: true
  }]
});
