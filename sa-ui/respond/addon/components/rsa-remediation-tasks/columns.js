// full list of columns to be used in the list-view
export default [
  {
    title: 'respond.remediationTasks.list.select',
    class: 'rsa-form-row-checkbox',
    width: '3%',
    field: 'selectItem',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true
  },
  {
    field: 'created',
    title: 'respond.remediationTasks.list.createdDate',
    width: '10%',
    dataType: 'date',
    visible: true
  },
  {
    field: 'priority',
    sortField: 'prioritySort',
    title: 'respond.remediationTasks.list.priority',
    width: '10%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'id',
    title: 'respond.remediationTasks.list.id',
    width: '5%',
    dataType: 'text',
    visible: true,
    disableSort: true
  },
  {
    field: 'name',
    title: 'respond.remediationTasks.list.name',
    width: '24%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'assignee',
    title: 'respond.remediationTasks.list.assignee',
    width: '13%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'status',
    sortField: 'statusSort',
    title: 'respond.remediationTasks.list.status',
    width: '7%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'lastUpdated',
    title: 'respond.remediationTasks.list.lastUpdated',
    width: '11%',
    dataType: 'date',
    visible: true
  },
  {
    field: 'createdBy',
    title: 'respond.remediationTasks.list.createdBy',
    width: '7%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'incidentId',
    title: 'respond.remediationTasks.list.incidentId',
    width: '10%',
    dataType: 'text',
    visible: true,
    disableSort: true
  }
];