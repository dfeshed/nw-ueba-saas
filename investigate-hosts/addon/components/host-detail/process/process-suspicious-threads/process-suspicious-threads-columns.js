// Process Suspicious threads details table header configuration

export default {
  windows: [{
    field: 'signature',
    title: 'investigateHosts.process.suspiciousThreads.signature',
    format: 'SIGNATURE',
    width: 50
  },
  {
    field: 'dllFileName',
    title: 'investigateHosts.process.suspiciousThreads.dllFileName'
  },
  {
    field: 'startAddress',
    title: 'investigateHosts.process.suspiciousThreads.startAddress'
  },
  {
    field: 'tid',
    title: 'investigateHosts.process.suspiciousThreads.tid'
  },
  {
    field: 'teb',
    title: 'investigateHosts.process.suspiciousThreads.teb',
    width: '24%'
  }]
};