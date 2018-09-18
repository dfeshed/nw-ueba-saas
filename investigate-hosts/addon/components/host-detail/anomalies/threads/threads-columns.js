// Anomalies > Thread table header configuration

export default {
  windows: [{
    field: 'startAddress',
    title: 'startAddress',
    width: 100
  },
  {
    field: 'fileName',
    title: 'dllFileName',
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
    field: 'process',
    title: 'process',
    width: 100
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: 100
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