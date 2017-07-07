export default {
  dataSourceGroup: 'Incidents',
  header: 'context.incident.header',
  footer: '',
  headerRequired: false,
  footerRequired: true,
  title: 'context.incident.title',
  sortColumn: 'created.$date',
  sortOrder: 'desc',
  columns: [
    {
      field: 'created.$date',
      title: 'context.incident.created',
      width: '15vw',
      dataType: 'datetime',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'priority',
      title: 'context.incident.priority',
      width: '5vw',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'averageAlertRiskScore',
      title: 'context.incident.averageAlertRiskScore',
      width: '5vw',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: '_id',
      title: 'context.incident._id',
      width: '5vw',
      dataType: 'link',
      path: '/respond/incident/{0}',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'name',
      title: 'context.incident.name',
      width: '15vw',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'status',
      title: 'context.incident.status',
      width: '5vw',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'assignee.name',
      title: 'context.incident.assignee',
      width: '8vw',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'alertCount',
      title: 'context.incident.alertCount',
      width: '5vw',
      icon: 'arrow-down-8',
      className: 'sort'
    }
  ]
};
