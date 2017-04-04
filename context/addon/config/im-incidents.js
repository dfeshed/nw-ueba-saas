export default {
  dataSourceGroup: 'Incidents',
  header: 'context.incident.header',
  footer: '',
  title: 'context.incident.title',
  columns: [
    {
      field: 'averageAlertRiskScore',
      title: 'context.incident.averageAlertRiskScore',
      width: '100',
      nested: 'averageAlertRiskScore',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      dataType: 'riskscore'
    },
    {
      field: '_id',
      title: 'context.incident._id',
      nested: '_id',
      width: '80',
      dataType: 'link',
      path: '/respond/incident/{0}'

    },
    {
      field: 'name',
      title: 'context.incident.name',
      width: '50',
      nested: ''
    },
    {
      field: 'created',
      title: 'context.incident.created',
      width: '100',
      dataType: 'datetime',
      nested: 'created.$date'
    },
    {
      field: 'status',
      title: 'context.incident.status',
      width: '100',
      nested: ''
    },
    {
      field: 'name',
      title: 'context.incident.assignee',
      width: '50',
      nested: 'assignee'
    },
    {
      field: 'priority',
      title: 'context.incident.priority',
      width: '50',
      nested: ''
    },
    {
      field: 'alertCount',
      title: 'context.incident.alertCount',
      width: '50',
      nested: ''
    }
  ]
};
