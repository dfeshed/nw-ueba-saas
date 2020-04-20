// Process Suspicious threads details table header configuration

export default {
  windows: [
    {
      field: 'dllFileName',
      title: 'investigateHosts.process.suspiciousThreads.dllFileName',
      width: '20vw',
      format: 'FILENAME'
    },
    {
      field: 'signature',
      title: 'investigateHosts.process.suspiciousThreads.signature',
      format: 'SIGNATURE',
      width: '20vw'
    },
    {
      field: 'startAddress',
      title: 'investigateHosts.process.suspiciousThreads.startAddress',
      width: '15vw'
    },
    {
      field: 'tid',
      title: 'investigateHosts.process.suspiciousThreads.tid',
      width: '8vw'
    },
    {
      field: 'teb',
      title: 'investigateHosts.process.suspiciousThreads.teb',
      width: '30vw'
    }]
};