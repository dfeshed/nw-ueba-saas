export default[
  {
    dataSourceGroup: 'Modules',
    header: 'context.modules.header',
    footer: ' ',
    title: 'context.modules.title',
    columns: [
      {
        field: 'Score',
        title: 'context.modules.iiocScore',
        dataType: 'riskscore',
        width: '10%',
        class: 'rsa-module-riskscore',
        componentClass: 'rsa-content-badge-score',
        nested: 'IOCScore.Score'
      },
      {
        field: 'Name',
        title: 'context.modules.moduleName',
        width: '25%',
        class: 'rsa-module-moduleName',
        nested: ''
      },
      {
        field: 'AnalyticsScore',
        title: 'context.modules.analyticsScore',
        width: '15%',
        class: 'rsa-module-riskScore',
        nested: ''
      },
      {
        field: 'GlobalMachineCount',
        title: 'context.modules.machineCount',
        width: '15%',
        class: 'rsa-module-machineCount',
        nested: ''
      },
      {
        field: 'Signature',
        title: 'context.modules.signature',
        width: '30%',
        class: 'rsa-module-signature',
        nested: ''
      }
    ]
  },

  {
    dataSourceGroup: 'IOC',
    header: 'context.iiocs.header',
    footer: '',
    title: 'context.iiocs.title',
    columns: [
      {
        field: 'LastExecuted',
        title: 'context.iiocs.lastExecuted',
        width: '25%',
        class: 'rsa-iioc-iiocLevel2',
        dataType: 'text',
        nested: ''
      },
      {
        field: 'IOCLevel',
        title: 'context.iiocs.iOCLevel',
        width: '25%',
        class: 'rsa-iioc-iiocLevel1',
        dataType: 'text',
        nested: ''
      },
      {
        field: 'Description',
        title: 'context.iiocs.description',
        width: '25%',
        class: 'rsa-iioc-iiocLevel0',
        dataType: 'text',
        nested: ''
      }
    ]
  }
];
