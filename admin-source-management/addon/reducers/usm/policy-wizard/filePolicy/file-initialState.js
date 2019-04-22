
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
    enabled: null // true or false
  },

  // define-policy-step - available settings to render the left col
  // * make sure the id, the end of label i18n key, and the field (policy property) are all the same
  availableSettings: [
  ]

};
