export default [
  {
    dataSourceGroup: 'Modules',
    header: 'context.modules.header',
    footer: ' ',
    title: 'context.modules.title',
    columns: [
      {
        field: 'IOCScore',
        title: 'context.modules.iiocScore',
        dataType: 'riskscore',
        width: '100',
        class: 'rsa-module-riskscore',
        componentClass: 'rsa-content-badge-score',
        nested: 'IOCScore.Score'
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
        width: '99',
        class: 'rsa-module-riskScore'
      },
      {
        field: 'GlobalMachineCount',
        title: 'context.modules.machineCount',
        width: '98',
        class: 'rsa-module-machineCount'
      },
      {
        field: 'Signature',
        title: 'context.modules.signature',
        width: '100',
        class: 'rsa-module-signature'
      }
    ]
  },

  {
    dataSourceGroup: 'IOC',
    header: 'context.iiocs.header',
    footer: '',
    title: 'context.iiocs.title',
    sortColumn: 'IOCLevel',
    columns: [
      {
        field: 'LastExecuted',
        title: 'context.iiocs.lastExecuted',
        width: '200',
        class: 'rsa-iioc-iiocLevel2',
        dataType: 'text',
        nested: ''
      },
      {
        field: 'IOCLevel',
        title: 'context.iiocs.iOCLevel',
        width: '200',
        class: 'rsa-iioc-iiocLevel1',
        dataType: 'text',
        nested: ''
      },
      {
        field: 'Description',
        title: 'context.iiocs.description',
        width: '200',
        class: 'rsa-iioc-iiocLevel0',
        dataType: 'text',
        nested: ''
      }
    ]
  }
];
