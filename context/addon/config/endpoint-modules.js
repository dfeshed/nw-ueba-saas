export default {
  dataSourceGroup: 'Modules',
  header: 'context.modules.header',
  footer: ' ',
  timeWindowRequired: false,
  title: 'context.modules.title',
  sortColumn: 'IOCScore.Score',
  sortOrder: 'descending',
  columns: [
    {
      field: 'IOCScore.Score',
      title: 'context.modules.iiocScore',
      dataType: 'riskscore',
      width: '12vh',
      class: 'rsa-module-riskscore',
      componentClass: 'rsa-content-badge-score'
    },
    {
      field: 'Name',
      title: 'context.modules.moduleName',
      width: '20vh',
      class: 'rsa-module-moduleName'
    },
    {
      field: 'AnalyticsScore',
      title: 'context.modules.analyticsScore',
      width: '10vh',
      class: 'rsa-module-riskScore'
    },
    {
      field: 'GlobalMachineCount',
      title: 'context.modules.machineCount',
      width: '10vh',
      class: 'rsa-module-machineCount'
    },
    {
      field: 'Signature',
      title: 'context.modules.signature',
      width: '38vh',
      class: 'rsa-module-signature'
    }
  ]
};
