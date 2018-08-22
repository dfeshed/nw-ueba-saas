// Anomalies > Thread table header configuration

export default {
  windows: [{
    field: 'startAddress',
    title: 'startAddress',
    width: '10%'
  },
  {
    field: 'fileName',
    title: 'dllFileName',
    width: '15%'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus',
    width: '10%'
  },
  {
    field: 'process',
    title: 'process',
    width: '20%'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: '10%'
  },
  {
    field: 'tid',
    title: 'tid',
    width: '5%'
  },
  {
    field: 'teb',
    title: 'teb',
    width: '23%'
  }]
};