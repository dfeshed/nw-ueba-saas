export default [
  {
    id: '__default_edr_policy',
    policyType: 'edrPolicy',
    name: 'Default EDR Policy',
    description: 'Default EDR Policy __default_edr_policy',
    dirty: false,
    defaultPolicy: true,
    associatedGroups: [],
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
    id: 'policy_001',
    policyType: 'edrPolicy',
    name: 'EMC 001',
    description: 'EMC 001 of policy policy_001',
    dirty: true,
    defaultPolicy: false,
    associatedGroups: [
      {
        referenceId: '5b7d886500319b5520f4b67d',
        name: 'Group 01'
      },
      {
        referenceId: '5b7d886500319b5520f4b672',
        name: 'Group 02'
      }
    ],
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
    id: 'policy_002',
    policyType: 'edrPolicy',
    name: 'EMC Reston! 012',
    description: 'EMC Reston 012 of policy policy_012',
    dirty: false,
    defaultPolicy: false,
    associatedGroups: [],
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
    id: 'policy_003',
    policyType: 'edrPolicy',
    name: 'EMC Bangalore! 013',
    description: 'EMC Bangalore 013 of policy policy_013',
    dirty: true,
    defaultPolicy: false,
    associatedGroups: [],
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
    policyType: 'edrPolicy',
    name: 'EMC Reston! 014',
    description: 'EMC Reston 014 of policy policy_014',
    dirty: false,
    defaultPolicy: false,
    associatedGroups: [],
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
