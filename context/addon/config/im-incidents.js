export default {
  dataSourceGroup: 'Incidents',
  header: 'context.incident.header',
  footer: '',
  timeWindowRequired: true,
  title: 'context.incident.title',
  sortColumn: 'created.$date',
  sortOrder: 'desc',
  columns: [
    {
      field: 'averageAlertRiskScore',
      title: 'context.incident.averageAlertRiskScore',
      width: '10vh',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      dataType: 'riskscore',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: '_id',
      title: 'context.incident._id',
      width: '10vh',
      dataType: 'link',
      path: '/respond/incident/{0}',
      icon: 'arrow-down-8',
      className: 'sort'

    },
    {
      field: 'name',
      title: 'context.incident.name',
      width: '20vh',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'created.$date',
      title: 'context.incident.created',
      width: '35vh',
      dataType: 'datetime',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'status',
      title: 'context.incident.status',
      width: '10vh',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'assignee.name',
      title: 'context.incident.assignee',
      width: '20vh',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'priority',
      title: 'context.incident.priority',
      width: '10vh',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'alertCount',
      title: 'context.incident.alertCount',
      width: '10vh',
      icon: 'arrow-down-8',
      className: 'sort'
    }
  ]
};
