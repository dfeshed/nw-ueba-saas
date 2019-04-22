
// initial state specific to Windows Log Source
export default {

  // the Windows Log source object to be created/updated/saved
  source: {
    // common source props (sourceType is special)
    // id: null,
    sourceType: 'windowsLogSource' // need a default for initialization

  },

  // define-source-step - available settings to render the left col
  // * make sure the id, the end of label i18n key, and the field (source property) are all the same
  availableSettings: [
  ]

};
