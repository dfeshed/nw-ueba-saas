const data = [
  'incident1',
  'country_dst',
  'Windows suspicious admin activity Shared object accessed',
  'SuspiciousEventIOC',
  'Reporting Engine - Source IP Exists',
  'RSA_01_Rule_HighConfidence',
  'RE Alert',
  'P2P software as detected by an Intrusion detection device',
  'Multi Service Connection Attempts Log NEW',
  'Multi Service Connection Attempts Log',
  'ModuleIOC 8',
  'ModuleIOC 7',
  'ModuleIOC 6',
  'ModuleIOC 2',
  'ModuleIOC 1',
  'Malware Found in Uploaded File(Miss secondary hit)',
  'Malware Found in Uploaded File(Miss primary hit)',
  'Malware Found in Uploaded File(High confidence)',
  'Malware Found in Network Session(Zero day)',
  'MachineIOC 7',
  'MachineIOC 4',
  'Log Event Users',
  'IPIOC 8',
  'IPIOC 4',
  'IPIOC 3',
  'IPIOC 1',
  'ESA - Source IP Exists',
  'ESA - GeoIP',
  'Direct Login To an Administrative Account',
  '5Fails1Success1Config change - Strict Pattern'
];

export default {
  subscriptionDestination: '/user/queue/alerts/distinct/names',
  requestDestination: '/ws/respond/alerts/distinct/names',
  message(/* frame */) {
    return {
      data,
      meta: {
        total: data.length
      }
    };
  }
};
