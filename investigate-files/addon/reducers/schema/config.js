/**
 * This config will help preferences details to render required preferences.
 *
 * There are four parts in config.
 *
 * modelName:: Determine which socket to fetch or save preferences.
 * fieldPrefix:: This is prefix used for proper translation of items.options. Used in preferences/preferences-details
 *   Ex: For options field 'size' to be properly translated, make sure 'investigateFiles.fields.size' (fieldPrefix+option)
 *   is present in translation file
 * addtionalFilterKey:: This property provide flexibility to send query data back for server API.
 * items: Preferences to display along with field details to pull field value from data json.
 * defaultPreferences: In case data is not pulled from server or first time preferences not available.
 * @public
 **/
export default {
  modelName: 'endpoint-preferences',
  fieldPrefix: 'investigateFiles.fields',
  items: [
    {
      name: 'preferences.endpoint-preferences.visibleColumns',
      type: 'multiSelect',
      options: [],
      field: 'filePreference.visibleColumns'
    },
    {
      name: 'preferences.endpoint-preferences.sortField',
      type: 'dropdown',
      options: [
        'firstFileName',
        'firstSeenTime',
        'machineOsType',
        'size',
        'format',
        'signature.features',
        'entropy',
        'pe.resources.company'
      ],
      field: 'filePreference.sortField'
    }
  ],
  defaultPreferences: {
    filePreference: {
      visibleColumns: ['firstFileName', 'firstSeenTime', 'machineOsType', 'signature.features', 'size', 'checksumSha256', 'entropy'],
      sortField: 'firstSeenTime'
    }
  }
};