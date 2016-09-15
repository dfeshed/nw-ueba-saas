/**
 * @file Column list for modules details
 * @public
 */
export default[
  {
    field: 'Score',
    title: 'context.modules.iiocScore',
    dataType: 'custom',
    width: '15%',
    class: 'rsa-module-riskscore',
    componentClass: 'rsa-content-badge-score',
    isDescending: true
  },
  {
    field: 'Name',
    title: 'context.modules.moduleName',
    width: '25%',
    class: 'rsa-module-moduleName',
    dataType: 'text'
  },
  {
    field: 'AnalyticsScore',
    title: 'context.modules.analyticsScore',
    width: '15%',
    class: 'rsa-module-riskScore',
    dataType: 'text'
  },
  {
    field: 'GlobalMachineCount',
    title: 'context.modules.machineCount',
    width: '15%',
    class: 'rsa-module-machineCount',
    dataType: 'text'
  },
  {
    field: 'Signature',
    title: 'context.modules.signature',
    width: '30%',
    class: 'rsa-module-signature',
    dataType: 'text'
  }
];