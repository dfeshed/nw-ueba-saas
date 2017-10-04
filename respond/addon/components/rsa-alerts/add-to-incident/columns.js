// Search Incidents Results Table columns (for associating alerts with an incident)
export default [
  {
    id: 'select',
    title: 'empty',
    width: '3%',
    disableSort: true
  },
  {
    field: 'id',
    title: 'respond.incidents.list.id',
    width: '5%',
    dataType: 'text',
    disableSort: true
  },
  {
    field: 'name',
    title: 'respond.incidents.list.name',
    width: '45%',
    dataType: 'text'
  },
  {
    field: 'created',
    title: 'respond.incidents.list.createdDate',
    width: '12%',
    dataType: 'date'
  },
  {
    field: 'assignee',
    title: 'respond.incidents.list.assignee',
    width: '20%',
    dataType: 'text'
  }
];
