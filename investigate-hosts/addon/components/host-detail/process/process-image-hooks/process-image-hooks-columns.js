// Process image hooks details table header configuration

export default {
  windows: [{
    field: 'type',
    title: 'investigateHosts.process.imageHooks.type',
    width: 50
  },
  {
    field: 'signature',
    title: 'investigateHosts.process.imageHooks.signature',
    format: 'SIGNATURE'
  },
  {
    field: 'dllFileName',
    title: 'investigateHosts.process.imageHooks.dllFileName'
  },
  {
    field: 'hookFileName',
    title: 'investigateHosts.process.imageHooks.hookFileName'
  },
  {
    field: 'symbol',
    title: 'investigateHosts.process.imageHooks.symbol',
    width: '19%'
  }]
};