export default {
  dataSourceGroup: 'Alerts',
  header: 'context.alerts.header',
  footer: '',
  title: 'context.alerts.title',
  columns: [
    {
      field: 'alert.risk_score',
      title: 'context.alerts.risk_score',
      width: '100',
      dataType: 'riskscore',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score'
    },
    {
      field: 'alert.source',
      title: 'context.alerts.source',
      width: '100'
    },
    {
      field: 'alert.name',
      title: 'context.alerts.name',
      width: '100',
      nested: '_id.$oid',
      dataType: 'link',
      path: '/respond/alert/{0}'
    },
    {
      field: 'alert.numEvents',
      title: 'context.alerts.numEvents',
      width: '100'
    },
    {
      field: 'alert.timestamp.$date.created',
      title: 'context.alerts.created',
      width: '100',
      dataType: 'datetime'
    },
    {
      field: 'incidentId',
      title: 'context.alerts.id',
      width: '80',
      dataType: 'link',
      path: '/respond/incident/{0}'
    }
  ]
};
