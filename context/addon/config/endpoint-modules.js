export default {
  dataSourceGroup: 'Modules',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'IOCScore.Score',
      title: 'context.modules.iiocScore',
      dataType: 'riskscore',
      width: '100',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score'
    },
    {
      field: 'Name',
      title: 'context.modules.moduleName',
      width: '100',
      class: 'rsa-module-moduleName'
    },
    {
      field: 'AnalyticsScore',
      title: 'context.modules.analyticsScore',
      width: '100',
      class: 'rsa-module-riskScore'
    },
    {
      field: 'GlobalMachineCount',
      title: 'context.modules.machineCount',
      width: '100',
      class: 'rsa-module-machineCount'
    },
    {
      field: 'Signature',
      title: 'context.modules.signature',
      width: '100',
      class: 'rsa-module-signature'
    }
  ]
};
