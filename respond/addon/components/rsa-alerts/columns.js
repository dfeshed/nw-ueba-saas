// full list of columns to be used in the list-view
export default [
  {
    title: 'respond.alerts.list.select',
    class: 'rsa-form-row-checkbox',
    width: '10%',
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
    width: '10%',
    dataType: 'number'
  },
  {
    field: 'alert.name',
    title: 'respond.alerts.list.name',
    width: '30%',
    dataType: 'text'
  },
  {
    field: 'alert.source',
    title: 'respond.alerts.list.source',
    width: '15%',
    dataType: 'text'
  },
  {
    field: 'alert.numEvents',
    title: 'respond.alerts.list.numEvents',
    width: '10%',
    dataType: 'number'
  },
  {
    field: 'incidentId',
    title: 'respond.alerts.list.incidentId',
    width: '15%',
    dataType: 'text',
    disableSort: true
  }
];
