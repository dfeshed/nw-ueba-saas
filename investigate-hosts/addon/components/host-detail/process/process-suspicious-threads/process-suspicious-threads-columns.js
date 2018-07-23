// Process Suspicious threads details table header configuration

export default {
  windows: [{
    field: 'signature',
    title: 'investigateHosts.process.suspiciousThreads.signature',
    width: 20
  },
  {
    field: 'dllFileName',
    title: 'investigateHosts.process.suspiciousThreads.dllFileName',
    width: 20
  },
  {
    field: 'startAddress',
    title: 'investigateHosts.process.suspiciousThreads.startAddress',
    width: 20
  },
  {
    field: 'tid',
    title: 'investigateHosts.process.suspiciousThreads.tid',
    width: 10
  },
  {
    field: 'teb',
    title: 'investigateHosts.process.suspiciousThreads.teb',
    width: 25
  }]
};