// full list of columns to be used in the list-view
export default [
  {
    title: 'respond.incidents.list.select',
    class: 'rsa-form-row-checkbox',
    width: '40',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true
  },
  {
    field: 'created',
    title: 'respond.incidents.list.createdDate',
    width: '100',
    class: 'rsa-respond-list-created',
    dataType: 'date',
    visible: true
  },
  {
    field: 'priority',
    title: 'respond.incidents.list.priority',
    width: '105',
    class: 'rsa-respond-list-priority',
    dataType: 'text',
    visible: true
  },
  {
    field: 'id',
    title: 'respond.incidents.list.id',
    class: 'rsa-respond-list-incident-id',
    width: '70',
    dataType: 'text',
    visible: true
  },
  {
    field: 'name',
    title: 'respond.incidents.list.name',
    width: '400',
    class: 'rsa-respond-list-name',
    dataType: 'text',
    visible: true
  },
  {
    field: 'status',
    title: 'respond.incidents.list.status',
    width: '100',
    class: 'rsa-respond-list-status',
    dataType: 'text',
    visible: true
  },
  {
    field: 'assignee',
    title: 'respond.incidents.list.assignee',
    width: '80',
    class: 'rsa-respond-list-assignee',
    dataType: 'text',
    visible: true
  },
  {
    field: 'alertCount',
    title: 'respond.incidents.list.alertCount',
    width: '50',
    class: 'rsa-respond-list-alertCount',
    dataType: 'number',
    visible: true
  },
  {
    field: 'sources',
    title: 'respond.incidents.list.sources',
    width: '100',
    class: 'rsa-respond-list-sources',
    dataType: 'text',
    visible: false
  },
  {
    field: 'eventCount',
    title: 'incident.fields.events',
    width: '50',
    class: 'rsa-respond-list-events',
    dataType: 'text',
    visible: false
  }
];