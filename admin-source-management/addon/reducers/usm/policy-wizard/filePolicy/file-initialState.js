
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
    sendTestLog: null // true or false
  },

  // define-policy-step - available settings to render the left col
  // * make sure the id, the end of label i18n key, and the field (policy property) are all the same
  availableSettings: [
    { index: 0, id: 'fileSettingsHeader', label: 'adminUsm.policyWizard.filePolicy.fileSettingsHeader', isHeader: true, isEnabled: true },
    { index: 1, id: 'enabled', label: 'adminUsm.policyWizard.filePolicy.enabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'enabled', value: false }] },
    { index: 2, id: 'sendTestLog', label: 'adminUsm.policyWizard.filePolicy.sendTestLog', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios', defaults: [{ field: 'sendTestLog', value: false }] }
  ]

};
