// Anomalies > Hooks table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'type',
    width: 15
  },
  {
    field: 'dllFileName',
    title: 'dllFileName',
    width: 20
  },
  {
    field: 'hookedProcess',
    title: 'hookedProcess',
    width: 20
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: 20
  },
  {
    field: 'symbol',
    title: 'hookedSymbol',
    width: 25
  }]
};