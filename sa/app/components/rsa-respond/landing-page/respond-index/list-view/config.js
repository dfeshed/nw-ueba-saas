/**
 * @file Configuration file for the list-view component.
 * @public
 */
import Ember from 'ember';

const {
  Object: EmberObject
} = Ember;

// full list of columns to be used in the list-view
export const availableColumnsConfig = [
  EmberObject.create({
    title: '',
    class: 'rsa-form-row-checkbox',
    width: '30',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  }),
  EmberObject.create({
    field: 'id',
    title: 'incident.list.id',
    class: 'rsa-respond-list-incident-id',
    width: '70',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'name',
    title: 'incident.list.name',
    width: '400',
    class: 'rsa-respond-list-name',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'riskScore',
    title: 'incident.list.riskScore',
    dataType: 'custom',
    width: '100',
    class: 'rsa-respond-list-riskscore',
    componentClass: 'rsa-content-badge-score',
    isDescending: true,
    visible: true
  }),
  EmberObject.create({
    field: 'prioritySort',
    title: 'incident.list.priority',
    width: '80',
    class: 'rsa-respond-list-priority',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'statusSort',
    title: 'incident.list.status',
    width: '100',
    class: 'rsa-respond-list-status',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'created',
    title: 'incident.list.createdDate',
    width: '90',
    class: 'rsa-respond-list-created',
    dataType: 'date-time',
    componentClass: 'rsa-content-datetime',
    visible: true
  }),
  EmberObject.create({
    field: 'assigneeName',
    title: 'incident.list.assignee',
    width: '80',
    class: 'rsa-respond-list-assignee',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'alertCount',
    title: 'incident.list.alertCount',
    width: '50',
    class: 'rsa-respond-list-alertCount',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'sources',
    title: 'incident.list.sources',
    width: '100',
    class: 'rsa-respond-list-sources',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'eventCount',
    title: 'incident.fields.events',
    width: '50',
    class: 'rsa-respond-list-events',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'groupBySourceIp',
    title: 'incident.fields.groupBySourceIp',
    width: '100',
    class: 'rsa-respond-list-sourceIps',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'groupByDestinationIp',
    title: 'incident.fields.groupByDestinationIp',
    width: '100',
    class: 'rsa-respond-list-destinationIps',
    dataType: 'text',
    visible: false
  })
];
