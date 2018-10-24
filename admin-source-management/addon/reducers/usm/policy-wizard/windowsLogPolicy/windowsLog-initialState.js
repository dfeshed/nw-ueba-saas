
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
    captureFloatingCode: null // true or false
  },

  // define-policy-step - available settings to render the left col
  // * make sure the id is always the same as the policy property name
  availableSettings: [
    { index: 0, id: 'captureFloatingCode', label: 'adminUsm.policyWizard.edrPolicy.captureFloatingCode', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/edr/edr-radios', defaults: [{ field: 'captureFloatingCode', value: true }] }
  ]

};
