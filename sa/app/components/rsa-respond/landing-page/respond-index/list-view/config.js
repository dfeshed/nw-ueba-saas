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
    field: 'id',
    title: 'incident.list.id',
    class: 'rsa-respond-list-incident-id',
    width: '7%',
    dataType: 'text',
    visible: true,
    displayIndex: 0
  }),
  EmberObject.create({
    field: 'name',
    title: 'incident.list.name',
    minWidth: '35%',
    class: 'rsa-respond-list-name',
    dataType: 'text',
    visible: true,
    displayIndex: 1
  }),
  EmberObject.create({
    field: 'riskScore',
    title: 'incident.list.riskScore',
    dataType: 'custom',
    width: '10%',
    class: 'rsa-respond-list-riskscore',
    componentClass: 'rsa-content-badge-score',
    isDescending: true,
    visible: true,
    displayIndex: 2
  }),
  EmberObject.create({
    field: 'prioritySort',
    title: 'incident.list.priority',
    width: '6%',
    class: 'rsa-respond-list-priority',
    dataType: 'text',
    visible: true,
    displayIndex: 3
  }),
  EmberObject.create({
    field: 'statusSort',
    title: 'incident.list.status',
    width: '12%',
    class: 'rsa-respond-list-status',
    dataType: 'text',
    visible: true,
    displayIndex: 4
  }),
  EmberObject.create({
    field: 'created',
    title: 'incident.list.createdDate',
    width: '11%',
    class: 'rsa-respond-list-created',
    dataType: 'date-time',
    componentClass: 'rsa-content-datetime',
    visible: true,
    displayIndex: 5
  }),
  EmberObject.create({
    field: 'assigneeFirstLastName',
    title: 'incident.list.assignee',
    minWidth: '10%',
    class: 'rsa-respond-list-assignee',
    dataType: 'text',
    visible: true,
    displayIndex: 6
  }),
  EmberObject.create({
    field: 'alertCount',
    title: 'incident.list.alertCount',
    width: '5%',
    class: 'rsa-respond-list-alertCount',
    dataType: 'text',
    visible: false,
    displayIndex: 7
  }),
  EmberObject.create({
    field: 'sources',
    title: 'incident.list.sources',
    width: '10%',
    class: 'rsa-respond-list-sources',
    dataType: 'text',
    visible: false,
    displayIndex: 8
  }),
  EmberObject.create({
    field: 'eventCount',
    title: 'incident.fields.events',
    width: '5%',
    class: 'rsa-respond-list-events',
    dataType: 'text',
    visible: false,
    displayIndex: 9
  })
];