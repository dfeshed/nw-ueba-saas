export default {
  dataSourceGroup: 'Alerts',
  header: 'context.alerts.header',
  footer: '',
  headerRequired: false,
  footerRequired: true,
  title: 'context.alerts.title',
  sortColumn: 'alert.timestamp.$date',
  sortOrder: 'desc',
  columns: [
    {
      field: 'alert.risk_score',
      title: 'context.alerts.risk_score',
      width: '5vw',
      dataType: 'riskscore',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'alert.source',
      title: 'context.alerts.source',
      width: '15vw',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'alert.name',
      title: 'context.alerts.name',
      width: '15vw',
      dataType: 'link',
      path: '/respond/alert/{0}',
      linkField: '_id.$oid',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'alert.numEvents',
      title: 'context.alerts.numEvents',
      width: '5vw',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'incidentId',
      title: 'context.alerts.id',
      width: '5vw',
      dataType: 'link',
      path: '/respond/incident/{0}',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'alert.timestamp.$date',
      title: 'context.alerts.created',
      width: '15vw',
      dataType: 'datetime',
      icon: 'arrow-down-8',
      className: 'sort'
    }
  ]
};
