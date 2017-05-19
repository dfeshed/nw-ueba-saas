export default {
  dataSourceGroup: 'Modules',
  header: 'context.modules.header',
  footer: ' ',
  timeWindowRequired: false,
  title: 'context.modules.title',
  sortColumn: 'IOCScore.Score',
  sortOrder: 'desc',
  columns: [
    {
      field: 'IOCScore.Score',
      title: 'context.modules.iiocScore',
      dataType: 'riskscore',
      width: '12vh',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'Name',
      title: 'context.modules.moduleName',
      width: '20vh',
      class: 'rsa-module-moduleName',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'AnalyticsScore',
      title: 'context.modules.analyticsScore',
      width: '17vh',
      class: 'rsa-module-riskScore',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'GlobalMachineCount',
      title: 'context.modules.machineCount',
      width: '15vh',
      class: 'rsa-module-machineCount',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'Signature',
      title: 'context.modules.signature',
      width: '38vh',
      class: 'rsa-module-signature',
      icon: 'arrow-down-8',
      className: 'sort'
    }
  ]
};
