// Process Suspicious threads details table header configuration

export default {
  windows: [{
    field: 'signature',
    title: 'investigateHosts.process.suspiciousThreads.signature',
    format: 'SIGNATURE',
    width: '19%'
  },
  {
    field: 'dllFileName',
    title: 'investigateHosts.process.suspiciousThreads.dllFileName',
    width: '19%'
  },
  {
    field: 'startAddress',
    title: 'investigateHosts.process.suspiciousThreads.startAddress',
    width: '19%'
  },
  {
    field: 'tid',
    title: 'investigateHosts.process.suspiciousThreads.tid',
    width: '10%'
  },
  {
    field: 'teb',
    title: 'investigateHosts.process.suspiciousThreads.teb',
    width: '24%'
  }]
};