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
    field: 'process',
    title: 'process',
    width: '20%'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: '15%'
  },
  {
    field: 'tid',
    title: 'tid',
    width: '10%'
  },
  {
    field: 'teb',
    title: 'teb',
    width: '23%'
  }]
};