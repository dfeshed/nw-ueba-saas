import moment from 'moment';

// initial state specific to EDR Policy
export default {

  // the EDR policy object to be created/updated/saved
  policy: {
    // common policy props (policyType is special)
    // id: null,
    policyType: 'edrPolicy', // need a default for initialization
    // name: null,
    // description: null,
    // etc...
    //
    // ==================================
    // start EDR Policy specific props
    // ==================================
    //
    // scheduleConfig
    scanType: null, // 'ENABLED' | 'DISABLED' (Run Scheduled Scan)
    // scheduleOptions
    scanStartDate: null, // YYYY-MM-DD
    scanStartTime: null, // '10:00'
    recurrenceInterval: null, // 1
    recurrenceUnit: null, // 'DAYS' | 'WEEKS'
    runOnDaysOfWeek: null, // array containing day name (names eventually) ex. ['MONDAY']
    // scanOptions
    cpuMax: null, // 75
    cpuMaxVm: null, // 85
    // captureFloatingCode: null, // true or false
    downloadMbr: null, // true or false
    // filterSignedHooks: null, // true or false
    requestScanOnRegistration: null, // true or false
    blockingEnabled: null, // true or false
    primaryAddress: null,
    primaryNwServiceId: null,
    primaryHttpsPort: null, // 1 to 65535
    primaryHttpsBeaconInterval: null, // 15 (900 secs is 15 mins)
    primaryHttpsBeaconIntervalUnit: null, // 'MINUTES' | 'HOURS'
    primaryUdpPort: null, // 1 to 65535
    primaryUdpBeaconInterval: null, // 30 (seconds)
    primaryUdpBeaconIntervalUnit: null, // 'SECONDS' | 'MINUTES'
    agentMode: null // 'NO_MONITORING' | 'FULL_MONITORING'
  },

  // define-policy-step - available settings to render the left col
  // * make sure the id is always the same as the policy property name
  availableSettings: [
    { index: 0, id: 'scanScheduleHeader', label: 'adminUsm.policyWizard.edrPolicy.scanSchedule', isHeader: true, isEnabled: true, isGreyedOut: true },
    { index: 1, id: 'scanType', label: 'adminUsm.policyWizard.edrPolicy.schedOrManScan', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'scanType', value: 'DISABLED' }] },
    { index: 2, id: 'scanStartDate', label: 'adminUsm.policyWizard.edrPolicy.effectiveDate', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] },
    { index: 3, id: 'recurrenceInterval', label: 'adminUsm.policyWizard.edrPolicy.scanFrequency', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/recurrence-interval', defaults: [{ field: 'recurrenceInterval', value: 1 }, { field: 'recurrenceUnit', value: 'DAYS' }] },
    { index: 4, id: 'scanStartTime', label: 'adminUsm.policyWizard.edrPolicy.startTime', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/start-time', defaults: [{ field: 'scanStartTime', value: '09:00' }] },
    { index: 5, id: 'cpuMax', label: 'adminUsm.policyWizard.edrPolicy.cpuMax', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/cpu-max', defaults: [{ field: 'cpuMax', value: 90 }] },
    { index: 6, id: 'cpuMaxVm', label: 'adminUsm.policyWizard.edrPolicy.vmMax', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/vm-max', defaults: [{ field: 'cpuMaxVm', value: 90 }] },
    { index: 7, id: 'advScanSettingsHeader', label: 'adminUsm.policyWizard.edrPolicy.advScanSettings', isHeader: true, isEnabled: true },
    /* Capture floating code is currently disabled for 11.3 since endpoint agent wont support them.
    { index: 8, id: 'captureFloatingCode', label: 'adminUsm.policyWizard.edrPolicy.captureFloatingCode', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'captureFloatingCode', value: true }] },
    */
    { index: 9, id: 'downloadMbr', label: 'adminUsm.policyWizard.edrPolicy.downloadMbr', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'downloadMbr', value: false }] },
    /* Include hooks with signed modules is currently disabled for 11.3 since endpoint agent wont support them.
    // { index: 10, id: 'filterSignedHooks', label: 'adminUsm.policyWizard.edrPolicy.filterSignedHooks', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'filterSignedHooks', value: false }] },
    */
    { index: 11, id: 'requestScanOnRegistration', label: 'adminUsm.policyWizard.edrPolicy.requestScanOnRegistration', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'requestScanOnRegistration', value: false }] },
    { index: 12, id: 'invActionsHeader', label: 'adminUsm.policyWizard.edrPolicy.invasiveActions', isHeader: true, isEnabled: true },
    { index: 13, id: 'blockingEnabled', label: 'adminUsm.policyWizard.edrPolicy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'blockingEnabled', value: false }] },
    { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', isHeader: true, isEnabled: true },
    { index: 15, id: 'primaryAddress', label: 'adminUsm.policyWizard.edrPolicy.primaryAddress', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/primary-address', defaults: [{ field: 'primaryAddress', value: '' }, { field: 'primaryNwServiceId', value: '' }] },
    { index: 16, id: 'primaryHttpsPort', label: 'adminUsm.policyWizard.edrPolicy.primaryHttpsPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-ports', defaults: [{ field: 'primaryHttpsPort', value: 443 }] },
    { index: 17, id: 'primaryHttpsBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-beacons', defaults: [{ field: 'primaryHttpsBeaconInterval', value: 15 }, { field: 'primaryHttpsBeaconIntervalUnit', value: 'MINUTES' }] },
    { index: 18, id: 'primaryUdpPort', label: 'adminUsm.policyWizard.edrPolicy.primaryUdpPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-ports', defaults: [{ field: 'primaryUdpPort', value: 444 }] },
    { index: 19, id: 'primaryUdpBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-beacons', defaults: [{ field: 'primaryUdpBeaconInterval', value: 30 }, { field: 'primaryUdpBeaconIntervalUnit', value: 'SECONDS' }] },
    { index: 20, id: 'agentSettingsHeader', label: 'adminUsm.policyWizard.edrPolicy.agentSettings', isHeader: true, isEnabled: true },
    { index: 21, id: 'agentMode', label: 'adminUsm.policyWizard.edrPolicy.agentMode', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'agentMode', value: 'FULL_MONITORING' }] }
  ]

};
