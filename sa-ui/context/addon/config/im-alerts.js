export default {
  dataSourceGroup: 'Alerts',
  header: 'context.alerts.header',
  footer: '',
  headerRequired: false,
  footerRequired: true,
  title: 'context.alerts.title',
  sortColumn: 'created',
  sortOrder: 'desc',
  columns: [
    {
      field: 'created',
      title: 'context.alerts.created',
      width: '15vw',
      dataType: 'datetime',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'riskScore',
      title: 'context.alerts.risk_score',
      width: '5vw',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'title',
      title: 'context.alerts.name',
      width: '15vw',
      dataType: 'link',
      path: '/respond/alert/{0}',
      linkField: 'id',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'source',
      title: 'context.alerts.source',
      width: '15vw',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'eventCount',
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
    }
  ]
};
