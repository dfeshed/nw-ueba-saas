export default {
  dataSourceGroup: 'Incidents',
  header: 'context.incident.header',
  footer: '',
  timeWindowRequired: true,
  title: 'context.incident.title',
  columns: [
    {
      field: 'averageAlertRiskScore',
      title: 'context.incident.averageAlertRiskScore',
      width: '8vh',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      dataType: 'riskscore'
    },
    {
      field: '_id',
      title: 'context.incident._id',
      width: '8vh',
      dataType: 'link',
      path: '/respond/incident/{0}'

    },
    {
      field: 'name',
      title: 'context.incident.name',
      width: '14vh'
    },
    {
      field: 'created.$date',
      title: 'context.incident.created',
      width: '20vh',
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
