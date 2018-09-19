// Anomalies > Hooks table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'type',
    width: 100
  },
  {
    field: 'dllFileName',
    title: 'dllFileName'
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
    field: 'hookedProcess',
    title: 'hookedProcess'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: '15%'
  },
  {
    field: 'symbol',
    title: 'hookedSymbol'
  }]
};