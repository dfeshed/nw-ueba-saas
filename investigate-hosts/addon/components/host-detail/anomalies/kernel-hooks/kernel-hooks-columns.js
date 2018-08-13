// Anomalies > kernel Hooks table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'type',
    width: '20%'
  },
  {
    field: 'dllFileName',
    title: 'dllFileName',
    width: '25%'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: '25%'
  },
  {
    field: 'hookedFileName',
    title: 'hookedFileName',
    width: '25%'
  }]
};