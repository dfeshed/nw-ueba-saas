// Process image hooks details table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'investigateHosts.process.imageHooks.type',
    width: '15%'
  },
  {
    field: 'signature',
    title: 'investigateHosts.process.imageHooks.signature',
    format: 'SIGNATURE',
    width: '15%'
  },
  {
    field: 'dllFileName',
    title: 'investigateHosts.process.imageHooks.dllFileName',
    width: '19%'
  },
  {
    field: 'hookFileName',
    title: 'investigateHosts.process.imageHooks.hookFileName',
    width: '19%'
  },
  {
    field: 'symbol',
    title: 'investigateHosts.process.imageHooks.symbol',
    width: '19%'
  }]
};