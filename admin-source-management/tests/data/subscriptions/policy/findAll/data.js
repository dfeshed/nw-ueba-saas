export default [
  {
    id: 'policy_001',
    type: 'edrPolicy',
    name: 'EMC 001',
    description: 'EMC 001 of policy policy_001',
    'dirty': true,
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
    id: 'policy_012',
    type: 'edrPolicy',
    name: 'EMC Reston! 012',
    description: 'EMC Reston 012 of policy policy_012',
    'dirty': false,
    scheduleConfig: {
      enabledScheduledScan: true,
      scheduleOptions: {
        scanStartDate: 1527489158739,
        scanStartTime: [10, 23],
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: ['MONDAY']
      },
      scanOptions: {
        cpuMaximum: 60,
        cpuMaximumOnVirtualMachine: 80
      }
    }
  },
  {
    id: 'policy_013',
    type: 'edrPolicy',
    name: 'EMC Bangalore! 013',
    description: 'EMC Bangalore 013 of policy policy_013',
    'dirty': true,
    scheduleConfig: {
      enabledScheduledScan: true,
      scheduleOptions: {
        scanStartDate: 1527489158739,
        scanStartTime: [9, 23],
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: ['MONDAY']
      },
      scanOptions: {
        cpuMaximum: 60,
        cpuMaximumOnVirtualMachine: 80
      }
    }
  },
  {
    id: 'policy_014',
    type: 'edrPolicy',
    name: 'EMC Reston! 014',
    description: 'EMC Reston 014 of policy policy_014',
    'dirty': false,
    scheduleConfig: {
      enabledScheduledScan: true,
      scheduleOptions: {
        scanStartDate: 1527489158739,
        scanStartTime: [11, 23],
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: ['THURSDAY']
      },
      scanOptions: {
        cpuMaximum: 65,
        cpuMaximumOnVirtualMachine: 90
      }
    }
  }
];
