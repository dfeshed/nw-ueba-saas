
// initial state specific to file Policy
export default {

  // the file policy object to be created/updated/saved
  policy: {
    // common policy props (policyType is special)
    // id: null,
    policyType: 'filePolicy', // need a default for initialization
    // name: null,
    // description: null,
    // etc...
    //
    // ==========================================
    // start file Policy specific props
    // ==========================================
    enabled: null, // true or false
    sendTestLog: null, // true or false
    primaryDestination: null,
    secondaryDestination: null,
    protocol: null // 'UDP' | 'TCP' | 'TLS'
  },

  steps: [
    {
      id: 'identifyPolicyStep',
      prevStepId: '',
      nextStepId: 'definePolicyStep',
      title: 'adminUsm.policyWizard.identifyPolicy',
      stepComponent: 'usm-policies/policy-wizard/identify-policy-step',
      titlebarComponent: 'usm-policies/policy-wizard/policy-titlebar',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar',
      prevButtonDisabled: true,
      nextButtonDisabled: false,
      saveButtonDisabled: true,
      publishButtonDisabled: true,
      showErrors: false
    },
    {
      id: 'definePolicyStep',
      prevStepId: 'identifyPolicyStep',
      nextStepId: 'definePolicySourcesStep',
      title: 'adminUsm.policyWizard.definePolicyConnection',
      stepComponent: 'usm-policies/policy-wizard/define-policy-step',
      titlebarComponent: 'usm-policies/policy-wizard/policy-titlebar',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar',
      prevButtonDisabled: false,
      nextButtonDisabled: false,
      saveButtonDisabled: false,
      publishButtonDisabled: false,
      showErrors: false
    },
    {
      id: 'definePolicySourcesStep',
      prevStepId: 'definePolicyStep',
      nextStepId: '',
      title: 'adminUsm.policyWizard.definePolicySources',
      stepComponent: 'usm-policies/policy-wizard/define-policy-sources-step',
      titlebarComponent: 'usm-policies/policy-wizard/policy-titlebar',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar',
      prevButtonDisabled: false,
      nextButtonDisabled: true,
      saveButtonDisabled: false,
      publishButtonDisabled: false,
      showErrors: false
    }
  ],

  // define-policy-step - available settings to render the left col
  // * make sure the id, the end of label i18n key, and the field (policy property) are all the same
  availableSettings: [
    { index: 0, id: 'fileSettingsHeader', label: 'adminUsm.policyWizard.filePolicy.fileSettingsHeader', isHeader: true, isEnabled: true },
    { index: 1, id: 'enabled', label: 'adminUsm.policyWizard.filePolicy.enabled', isEnabled: true, isGreyedOut: false, headerId: 'fileSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'enabled', value: false }] },
    { index: 2, id: 'sendTestLog', label: 'adminUsm.policyWizard.filePolicy.sendTestLog', isEnabled: true, isGreyedOut: false, headerId: 'fileSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'sendTestLog', value: false }] },
    // re-using these components from windows-log
    { index: 3, id: 'primaryDestination', label: 'adminUsm.policyWizard.filePolicy.primaryDestination', isEnabled: true, isGreyedOut: false, headerId: 'fileSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', defaults: [{ field: 'primaryDestination', value: '' }] },
    { index: 4, id: 'secondaryDestination', label: 'adminUsm.policyWizard.filePolicy.secondaryDestination', isEnabled: true, isGreyedOut: false, headerId: 'fileSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', defaults: [{ field: 'secondaryDestination', value: '' }] },
    { index: 5, id: 'protocol', label: 'adminUsm.policyWizard.filePolicy.protocol', isEnabled: true, isGreyedOut: false, headerId: 'fileSettingsHeader', parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-protocol', defaults: [{ field: 'protocol', value: 'TCP' }] }
  ]

};
