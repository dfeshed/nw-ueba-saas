export default [
  {
    id: 'policy_001',
    name: 'EMC 001',
    description: 'EMC 001 of policy policy_001',
    scheduleConfig: {
      enabledScheduledScan: true,
      scheduleOptions: {
        scanStartDate: 1527489158739,
        scanStartTime: [10, 23],
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: ['TUESDAY']
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },
  {
    'id': 'policy_012',
    'name': 'EMC Reston! 012',
    'description': 'EMC Reston 012 of policy policy_012'
  }
];
