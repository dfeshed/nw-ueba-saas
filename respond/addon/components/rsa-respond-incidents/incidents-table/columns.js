// full list of columns to be used in the list-view
export default [
  {
    title: 'respond.incidents.list.select',
    class: 'rsa-form-row-checkbox',
    width: '10%',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true
  },
  {
    field: 'created',
    title: 'respond.incidents.list.createdDate',
    width: '12%',
    dataType: 'date',
    visible: true
  },
  {
    field: 'priority',
    title: 'respond.incidents.list.priority',
    width: '10%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'id',
    title: 'respond.incidents.list.id',
    width: '8%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'name',
    title: 'respond.incidents.list.name',
    width: '25%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'status',
    title: 'respond.incidents.list.status',
    width: '15%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'assignee',
    title: 'respond.incidents.list.assignee',
    width: '15%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'alertCount',
    title: 'respond.incidents.list.alertCount',
    width: '5%',
    dataType: 'number',
    visible: true
  }
];