
// initial state specific to Windows Log Policy
export default {

  // the Windows Log policy object to be created/updated/saved
  policy: {
    // common policy props (policyType is special)
    // id: null,
    policyType: 'windowsLogPolicy', // need a default for initialization
    // name: null,
    // description: null,
    // etc...
    //
    // ==========================================
    // start Windows Log Policy specific props
    // ==========================================
    enabled: null, // true or false
    sendTestLog: null, // true or false
    primaryDestination: null,
    secondaryDestination: null,
    protocol: null, // 'UDP' | 'TCP' | 'TLS'
    channelFilters: null, // []
    customConfig: null
  },

  // define-policy-step - available settings to render the left col
  // * make sure the id, the end of label i18n key, and the field (policy property) are all the same
  availableSettings: [
    { index: 0, id: 'windowsLogSettingsHeader', label: 'adminUsm.policyWizard.windowsLogPolicy.windowsLogSettingsHeader', isHeader: true, isEnabled: true },
    { index: 1, id: 'enabled', label: 'adminUsm.policyWizard.windowsLogPolicy.enabled', isEnabled: true, isGreyedOut: false, headerId: 'windowsLogSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'enabled', value: false }] },
    { index: 2, id: 'sendTestLog', label: 'adminUsm.policyWizard.windowsLogPolicy.sendTestLog', isEnabled: true, isGreyedOut: false, headerId: 'windowsLogSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'sendTestLog', value: false }] },
    { index: 3, id: 'primaryDestination', label: 'adminUsm.policyWizard.windowsLogPolicy.primaryDestination', isEnabled: true, isGreyedOut: false, headerId: 'windowsLogSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', defaults: [{ field: 'primaryDestination', value: '' }] },
    { index: 4, id: 'secondaryDestination', label: 'adminUsm.policyWizard.windowsLogPolicy.secondaryDestination', isEnabled: true, isGreyedOut: false, headerId: 'windowsLogSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', defaults: [{ field: 'secondaryDestination', value: '' }] },
    { index: 5, id: 'protocol', label: 'adminUsm.policyWizard.windowsLogPolicy.protocol', isEnabled: true, isGreyedOut: false, headerId: 'windowsLogSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-protocol', defaults: [{ field: 'protocol', value: 'TCP' }] },
    { index: 6, id: 'channelFiltersSettingsHeader', label: 'adminUsm.policyWizard.windowsLogPolicy.channelFiltersSettingsHeader', isHeader: true, isEnabled: true },
    { index: 7, id: 'channelFilters', label: 'adminUsm.policyWizard.windowsLogPolicy.channelFilters', isEnabled: true, isGreyedOut: false, headerId: 'channelFiltersSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters', defaults: [{ field: 'channelFilters', value: [{ channel: '', filterType: 'INCLUDE', eventId: 'ALL' }] }] },
    { index: 8, id: 'advancedConfigHeader', label: 'adminUsm.policyWizard.windowsLogPolicy.advancedConfig', isHeader: true, isEnabled: true },
    { index: 9, id: 'customConfig', label: 'adminUsm.policyWizard.windowsLogPolicy.customConfig', isEnabled: true, isGreyedOut: false, headerId: 'advancedConfigHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/custom-config', defaults: [{ field: 'customConfig', value: '' }] }
  ]

};
