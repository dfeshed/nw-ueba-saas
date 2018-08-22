// Anomalies > kernel Hooks table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'type',
    width: '15%'
  },
  {
    field: 'driverFileName',
    title: 'driverFileName',
    width: '20%'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus',
    width: '10%'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: '15%'
  },
  {
    field: 'objectFunction',
    title: 'objectFunction',
    width: '15%'
  },
  {
    field: 'hookedFileName',
    title: 'hookedFileName',
    width: '15%'
  }]
};