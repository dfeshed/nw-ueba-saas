export default {
  dataSourceGroup: 'Alerts',
  header: 'context.alerts.header',
  footer: '',
  timeWindowRequired: true,
  title: 'context.alerts.title',
  sortColumn: 'alert.timestamp.$date.created',
  sortOrder: 'descending',
  columns: [
    {
      field: 'alert.risk_score',
      title: 'context.alerts.risk_score',
      width: '10vh',
      dataType: 'riskscore',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score'
    },
    {
      field: 'alert.source',
      title: 'context.alerts.source',
      width: '20vh'
    },
    {
      field: 'alert.name',
      title: 'context.alerts.name',
      width: '20vh',
      dataType: 'link',
      path: '/respond/alert/{0}',
      linkField: '_id.$oid'
    },
    {
      field: 'alert.numEvents',
      title: 'context.alerts.numEvents',
      width: '10vh'
    },
    {
      field: 'incidentId',
      title: 'context.alerts.id',
      width: '15vh',
      dataType: 'link',
      path: '/respond/incident/{0}'
    },
    {
      field: 'alert.timestamp.$date.created',
      title: 'context.alerts.created',
      width: '40vh',
      dataType: 'datetime'
    }
  ]
};
