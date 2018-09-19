// Anomalies > kernel Hooks table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'type',
    width: 100
  },
  {
    field: 'driverFileName',
    title: 'driverFileName'
  },
  {
    field: 'fileProperties.score',
    title: 'score'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE'
  },
  {
    field: 'objectFunction',
    title: 'objectFunction',
    width: '15%'
  },
  {
    field: 'hookedFileName',
    title: 'hookedFileName'
  }]
};