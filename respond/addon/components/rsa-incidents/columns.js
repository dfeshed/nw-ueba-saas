// full list of columns to be used in the list-view
export default [
  {
    title: 'respond.incidents.list.select',
    class: 'rsa-form-row-checkbox',
    width: '40px',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    disableSort: true
  },
  {
    field: 'created',
    title: 'respond.incidents.list.createdDate',
    width: '12%',
    dataType: 'date'
  },
  {
    field: 'priority',
    sortField: 'prioritySort',
    title: 'respond.incidents.list.priority',
    width: '7%',
    dataType: 'text'
  },
  {
    field: 'riskScore',
    title: 'respond.incidents.list.riskScore',
    width: '7%',
    dataType: 'number'
  },
  {
    field: 'id',
    title: 'respond.incidents.list.id',
    width: '8%',
    dataType: 'text',
    disableSort: true
  },
  {
    field: 'name',
    title: 'respond.incidents.list.name',
    width: '25%',
    dataType: 'text'
  },
  {
    field: 'status',
    sortField: 'statusSort',
    title: 'respond.incidents.list.status',
    width: '15%',
    dataType: 'text'
  },
  {
    field: 'assignee',
    title: 'respond.incidents.list.assignee',
    width: '15%',
    dataType: 'text'
  },
  {
    field: 'alertCount',
    title: 'respond.incidents.list.alertCount',
    width: '5%',
    dataType: 'number'
  }
];
