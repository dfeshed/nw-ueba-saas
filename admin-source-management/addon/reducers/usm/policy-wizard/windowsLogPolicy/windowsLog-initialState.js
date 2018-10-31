
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
    secondaryDestination: null
  },

  // define-policy-step - available settings to render the left col
  // * make sure the id is always the same as the policy property name
  availableSettings: [
    { index: 0, id: 'windowsLogSettingsHeader', label: 'adminUsm.policyWizard.windowsLogPolicy.windowsLogSettingsHeader', isHeader: true, isEnabled: true },
    { index: 1, id: 'enabled', label: 'adminUsm.policyWizard.windowsLogPolicy.enabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-radios', defaults: [{ field: 'enabled', value: false }] },
    { index: 2, id: 'sendTestLog', label: 'adminUsm.policyWizard.windowsLogPolicy.sendTestLog', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-radios', defaults: [{ field: 'sendTestLog', value: false }] },
    { index: 3, id: 'primaryDestination', label: 'adminUsm.policyWizard.windowsLogPolicy.primaryDestination', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', defaults: [{ field: 'primaryDestination', value: '' }] },
    { index: 4, id: 'secondaryDestination', label: 'adminUsm.policyWizard.windowsLogPolicy.secondaryDestination', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', defaults: [{ field: 'secondaryDestination', value: '' }] }
    // 4, 5, 6
    // { index: 7, id: 'channelFiltersSettingsHeader', label: 'adminUsm.policyWizard.windowsLogPolicy.channelFiltersSettingsHeader', isHeader: true, isEnabled: true }
    // 8 - channelFilters
  ]

};
