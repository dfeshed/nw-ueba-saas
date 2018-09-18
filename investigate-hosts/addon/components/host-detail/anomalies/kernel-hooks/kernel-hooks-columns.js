// Anomalies > kernel Hooks table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'type',
    width: 100
  },
  {
    field: 'driverFileName',
    title: 'driverFileName',
    width: 100
  },
  {
    field: 'fileProperties.score',
    title: 'score',
    width: '10%'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus',
    width: 100
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: 100
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