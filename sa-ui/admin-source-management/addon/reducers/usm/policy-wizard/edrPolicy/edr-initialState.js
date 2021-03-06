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
    cpuMax: null, // 25
    cpuMaxVm: null, // 10
    // captureFloatingCode: null, // true or false
    scanMbr: null, // true or false
    // filterSignedHooks: null, // true or false
    requestScanOnRegistration: null, // true or false
    blockingEnabled: null, // true or false
    isolationEnabled: null, // true or false
    maxFileDownloadSizeUnit: null, // KB or MB
    maxFileDownloadSize: null, // number, default 1
    fileDownloadEnabled: null, // true or false
    fileDownloadCriteria: null, // Unsigned
    primaryAddress: null,
    primaryNwServiceId: null,
    primaryAlias: null,
    primaryHttpsPort: null, // 1 to 65535
    primaryHttpsBeaconInterval: null, // 15 (900 secs is 15 mins)
    primaryHttpsBeaconIntervalUnit: null, // 'MINUTES' | 'HOURS'
    primaryUdpPort: null, // 1 to 65535
    primaryUdpBeaconInterval: null, // 30 (seconds)
    primaryUdpBeaconIntervalUnit: null, // 'SECONDS' | 'MINUTES'
    agentMode: null, // 'INSIGHTS' | 'ADVANCED'
    customConfig: null // free text
  },

  // define-policy-step - available settings to render the left col
  // * make sure the id, the end of label i18n key, and the field (policy property) are all the same
  availableSettings: [
    // Scan Schedule settings
    { index: 0, id: 'scanScheduleHeader', label: 'adminUsm.policyWizard.edrPolicy.scanSchedule', isHeader: true, isEnabled: true, isGreyedOut: true },
    { index: 1, id: 'scanType', label: 'adminUsm.policyWizard.edrPolicy.scanType', isEnabled: true, isGreyedOut: false, headerId: 'scanScheduleHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'scanType', value: 'DISABLED' }] },
    { index: 2, id: 'scanStartDate', label: 'adminUsm.policyWizard.edrPolicy.scanStartDate', isEnabled: true, isGreyedOut: true, headerId: 'scanScheduleHeader', parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] },
    { index: 3, id: 'recurrenceInterval', label: 'adminUsm.policyWizard.edrPolicy.recurrenceInterval', isEnabled: true, isGreyedOut: true, headerId: 'scanScheduleHeader', parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/recurrence-interval', defaults: [{ field: 'recurrenceInterval', value: 1 }, { field: 'recurrenceUnit', value: 'WEEKS' }, { field: 'runOnDaysOfWeek', value: ['MONDAY'] }] },
    { index: 4, id: 'scanStartTime', label: 'adminUsm.policyWizard.edrPolicy.scanStartTime', isEnabled: true, isGreyedOut: true, headerId: 'scanScheduleHeader', parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/start-time', defaults: [{ field: 'scanStartTime', value: '09:00' }] },
    { index: 5, id: 'cpuMax', label: 'adminUsm.policyWizard.edrPolicy.cpuMax', isEnabled: true, isGreyedOut: true, headerId: 'scanScheduleHeader', parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/cpu-max', defaults: [{ field: 'cpuMax', value: 25 }] },
    { index: 6, id: 'cpuMaxVm', label: 'adminUsm.policyWizard.edrPolicy.cpuMaxVm', isEnabled: true, isGreyedOut: true, headerId: 'scanScheduleHeader', parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/vm-max', defaults: [{ field: 'cpuMaxVm', value: 10 }] },
    //
    // Agent Mode settings
    { index: 7, id: 'agentSettingsHeader', label: 'adminUsm.policyWizard.edrPolicy.agentSettings', isHeader: true, isEnabled: true },
    { index: 8, id: 'agentMode', label: 'adminUsm.policyWizard.edrPolicy.agentMode', isEnabled: true, isGreyedOut: false, headerId: 'agentSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'agentMode', value: 'ADVANCED' }] },
    //
    // Scan settings
    { index: 9, id: 'advScanSettingsHeader', label: 'adminUsm.policyWizard.edrPolicy.advScanSettings', isHeader: true, isEnabled: true },
    /* Capture floating code is currently disabled for 11.3 since endpoint agent wont support them.
    { index: 10, id: 'captureFloatingCode', label: 'adminUsm.policyWizard.edrPolicy.captureFloatingCode', isEnabled: true, isGreyedOut: false, headerId: 'advScanSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'captureFloatingCode', value: true }] },
    */
    { index: 11, id: 'scanMbr', label: 'adminUsm.policyWizard.edrPolicy.scanMbr', isEnabled: true, isGreyedOut: false, headerId: 'advScanSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'scanMbr', value: false }] },
    /* Include hooks with signed modules is currently disabled for 11.3 since endpoint agent wont support them.
    // { index: 12, id: 'filterSignedHooks', label: 'adminUsm.policyWizard.edrPolicy.filterSignedHooks', isEnabled: true, isGreyedOut: false, headerId: 'advScanSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'filterSignedHooks', value: false }] },
    */
    { index: 13, id: 'requestScanOnRegistration', label: 'adminUsm.policyWizard.edrPolicy.requestScanOnRegistration', isEnabled: true, isGreyedOut: false, headerId: 'advScanSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'requestScanOnRegistration', value: false }] },
    //
    // Download settings
    { index: 14, id: 'downloadSettingsHeader', label: 'adminUsm.policyWizard.edrPolicy.downloadSettings', isHeader: true, isEnabled: true },
    { index: 15, id: 'fileDownloadEnabled', label: 'adminUsm.policyWizard.edrPolicy.automaticFileDownloads', isEnabled: true, isGreyedOut: false, headerId: 'downloadSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'fileDownloadEnabled', value: true }] },
    { index: 16, id: 'fileDownloadCriteria', label: 'adminUsm.policyWizard.edrPolicy.signature', isEnabled: true, isGreyedOut: false, headerId: 'downloadSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/signature-dropdown', defaults: [{ field: 'fileDownloadCriteria', value: 'Unsigned' }] },
    { index: 17, id: 'maxFileDownloadSize', label: 'adminUsm.policyWizard.edrPolicy.fileSizeLimit', isEnabled: true, isGreyedOut: false, headerId: 'downloadSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/file-size-selection', defaults: [{ field: 'maxFileDownloadSize', value: 1 }, { field: 'maxFileDownloadSizeUnit', value: 'MB' }] },
    //
    // Response Action settings
    { index: 18, id: 'invActionsHeader', label: 'adminUsm.policyWizard.edrPolicy.invasiveActions', isHeader: true, isEnabled: true },
    { index: 19, id: 'blockingEnabled', label: 'adminUsm.policyWizard.edrPolicy.blockingEnabled', isEnabled: true, isGreyedOut: false, headerId: 'invActionsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] },
    { index: 20, id: 'isolationEnabled', label: 'adminUsm.policyWizard.edrPolicy.isolationEnabled', isEnabled: true, isGreyedOut: false, headerId: 'invActionsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios-with-warning', defaults: [{ field: 'isolationEnabled', value: false }] },
    //
    // Endpoint server settings
    { index: 21, id: 'endpointServerHeader', label: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', isHeader: true, isEnabled: true },
    { index: 22, id: 'primaryAddress', label: 'adminUsm.policyWizard.edrPolicy.primaryAddress', isEnabled: true, isGreyedOut: false, headerId: 'endpointServerHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/primary-address', defaults: [{ field: 'primaryAddress', value: '' }, { field: 'primaryNwServiceId', value: '' }, { field: 'primaryAlias', value: '' }] },
    { index: 23, id: 'primaryHttpsPort', label: 'adminUsm.policyWizard.edrPolicy.primaryHttpsPort', isEnabled: true, isGreyedOut: false, headerId: 'endpointServerHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-ports', defaults: [{ field: 'primaryHttpsPort', value: 443 }] },
    { index: 24, id: 'primaryHttpsBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval', isEnabled: true, isGreyedOut: false, headerId: 'endpointServerHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-beacons', defaults: [{ field: 'primaryHttpsBeaconInterval', value: 15 }, { field: 'primaryHttpsBeaconIntervalUnit', value: 'MINUTES' }] },
    { index: 25, id: 'primaryUdpPort', label: 'adminUsm.policyWizard.edrPolicy.primaryUdpPort', isEnabled: true, isGreyedOut: false, headerId: 'endpointServerHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-ports', defaults: [{ field: 'primaryUdpPort', value: 444 }] },
    { index: 26, id: 'primaryUdpBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval', isEnabled: true, isGreyedOut: false, headerId: 'endpointServerHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-beacons', defaults: [{ field: 'primaryUdpBeaconInterval', value: 30 }, { field: 'primaryUdpBeaconIntervalUnit', value: 'SECONDS' }] },
    //
    // Advanced Configuration settings
    { index: 27, id: 'advancedConfigHeader', label: 'adminUsm.policyWizard.edrPolicy.advancedConfig', isHeader: true, isEnabled: true },
    { index: 28, id: 'customConfig', label: 'adminUsm.policyWizard.edrPolicy.customConfig', isEnabled: true, isGreyedOut: false, headerId: 'advancedConfigHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/custom-config', defaults: [{ field: 'customConfig', value: '' }] }
  ]

};