// Anomalies > Hooks table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'type',
    width: '10%'
  },
  {
    field: 'dllFileName',
    title: 'dllFileName',
    width: '15%'
  },
  {
    field: 'fileProperties.score',
    title: 'score',
    width: '10%'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus',
    width: '10%'
  },
  {
    field: 'hookedProcess',
    title: 'hookedProcess',
    width: '20%'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: '15%'
  },
  {
    field: 'symbol',
    title: 'hookedSymbol',
    width: '24%'
  }]
};