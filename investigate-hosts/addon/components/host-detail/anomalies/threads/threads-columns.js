// Anomalies > Thread table header configuration

export default {
  windows: [{
    field: 'startAddress',
    title: 'startAddress',
    width: 100
  },
  {
    field: 'fileName',
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
    field: 'process',
    title: 'process'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE'
  },
  {
    field: 'tid',
    title: 'tid',
    width: '15%'
  },
  {
    field: 'teb',
    title: 'teb'
  }]
};