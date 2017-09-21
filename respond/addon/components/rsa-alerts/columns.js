// full list of columns to be used in the list-view
export default [
  {
    title: 'respond.alerts.list.select',
    class: 'rsa-form-row-checkbox',
    width: '40px',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    disableSort: true
  },
  {
    field: 'receivedTime',
    title: 'respond.alerts.list.receivedTime',
    width: '12%',
    dataType: 'date'
  },
  {
    field: 'alert.severity',
    title: 'respond.alerts.list.severity',
    width: '90px',
    dataType: 'number'
  },
  {
    field: 'alert.name',
    title: 'respond.alerts.list.name',
    width: '20%',
    dataType: 'text'
  },
  {
    field: 'alert.source',
    title: 'respond.alerts.list.source',
    width: '150px',
    dataType: 'text'
  },
  {
    field: 'alert.numEvents',
    title: 'respond.alerts.list.numEvents',
    width: '120px',
    dataType: 'number'
  },
  {
    field: 'alert.host_summary',
    title: 'respond.alerts.list.hostSummary',
    width: '15%',
    dataType: 'text',
    disableSort: true
  },
  {
    field: 'incidentId',
    title: 'respond.alerts.list.incidentId',
    width: '120px',
    dataType: 'text',
    disableSort: true
  }
];
