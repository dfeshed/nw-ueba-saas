export default {
  dataSourceGroup: 'Incidents',
  header: 'context.incident.header',
  footer: '',
  timeWindowRequired: true,
  title: 'context.incident.title',
  sortColumn: 'created.$date',
  sortOrder: 'descending',
  columns: [
    {
      field: 'averageAlertRiskScore',
      title: 'context.incident.averageAlertRiskScore',
      width: '10vh',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      dataType: 'riskscore'
    },
    {
      field: '_id',
      title: 'context.incident._id',
      width: '7vh',
      dataType: 'link',
      path: '/respond/incident/{0}'

    },
    {
      field: 'name',
      title: 'context.incident.name',
      width: '20vh'
    },
    {
      field: 'created.$date',
      title: 'context.incident.created',
      width: '25vh',
      dataType: 'datetime'
    },
    {
      field: 'status',
      title: 'context.incident.status',
      width: '10vh'
    },
    {
      field: 'assignee.name',
      title: 'context.incident.assignee',
      width: '20vh'
    },
    {
      field: 'priority',
      title: 'context.incident.priority',
      width: '10vh'
    },
    {
      field: 'alertCount',
      title: 'context.incident.alertCount',
      width: '10vh'
    }
  ]
};
