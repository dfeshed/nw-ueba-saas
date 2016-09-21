export default[
  {
    field: 'averageAlertRiskScore',
    title: 'context.incident.averageAlertRiskScore',
    width: '9%',
    class: 'rsa-module-riskscore',
    componentClass: 'rsa-content-badge-score'
  },
  {
    field: '_id',
    title: 'context.incident._id',
    width: '7%'
  },
  {
    field: 'name',
    title: 'context.incident.name',
    width: '24%'
  },
  {
    field: 'created',
    title: 'context.incident.created',
    width: '20%'
  },
  {
    field: 'status',
    title: 'context.incident.status',
    width: '9%'
  },

  {
    field: 'assignee',
    title: 'context.incident.assignee',
    width: '9%'
  },
  {
    field: 'priority',
    title: 'context.incident.priority',
    width: '9%'
  },
  {
    field: 'alertCount',
    title: 'context.incident.alertCount',
    width: '8%'
  }
];