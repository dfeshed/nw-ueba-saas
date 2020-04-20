export default {
  dataSourceGroup: 'Modules',
  header: 'context.modules.header',
  footer: ' ',
  headerRequired: true,
  footerRequired: false,
  title: 'context.modules.title',
  sortColumn: 'IOCScore.Score',
  sortOrder: 'desc',
  columns: [
    {
      field: 'IOCScore.Score',
      title: 'context.modules.iiocScore',
      dataType: 'riskscore',
      width: '5vw',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'Name',
      title: 'context.modules.moduleName',
      width: '8vw',
      class: 'rsa-module-moduleName',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'AnalyticsScore',
      title: 'context.modules.analyticsScore',
      width: '8vw',
      class: 'rsa-module-riskScore',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'GlobalMachineCount',
      title: 'context.modules.machineCount',
      width: '8vw',
      class: 'rsa-module-machineCount',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'Signature',
      title: 'context.modules.signature',
      width: '8vw',
      class: 'rsa-module-signature',
      icon: 'arrow-down-8',
      className: 'sort'
    }
  ]
};
