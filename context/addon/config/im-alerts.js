export default {
  dataSourceGroup: 'Alerts',
  header: 'context.alerts.header',
  footer: '',
  title: 'context.alerts.title',
  columns: [
    {
      field: 'risk_score',
      title: 'context.alerts.risk_score',
      width: '100',
      dataType: 'riskscore',
      nested: 'alert.risk_score',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score'
    },
    {
      field: 'source',
      title: 'context.alerts.source',
      nested: 'alert',
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
      field: 'numEvents',
      title: 'context.alerts.numEvents',
      width: '100',
      nested: 'alert'
    },
    {
      field: 'created',
      title: 'context.alerts.created',
      width: '100',
      dataType: 'datetime',
      nested: 'alert.timestamp.$date'
    },
    {
      field: 'incidentId',
      title: 'context.alerts.id',
      width: '80',
      nested: 'incidentId',
      dataType: 'link',
      path: '/respond/incident/{0}'
    }
  ]
};
