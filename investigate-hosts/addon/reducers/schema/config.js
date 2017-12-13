/**
 * This config will help preferences details to render required preferences.
 *
 * There are four parts in config.
 *
 * modelName:: Determine which socket to fetch or save preferences.
 * fieldPrefix:: This is prefix used for proper translation of items.options. Used in preferences/preferences-details
 *   Ex: For options field 'agentId' to be properly translated, make sure 'investigateHosts.hosts.column.agentId' (fieldPrefix+option)
 *   is present in translation file
 * addtionalFilterKey:: This property provide flexibility to send query data back for server API.
 * items: Preferences to display along with field details to pull field value from data json.
 * defaultPreferences: In case data is not pulled from server or first time preferences not available.
 * @public
 **/
export default {
  modelName: 'endpoint-preferences',
  fieldPrefix: 'investigateHosts.hosts.column',
  items: [
    {
      name: 'preferences.endpoint-preferences.visibleColumns',
      type: 'multiSelect',
      options: [],
      field: 'machinePreference.visibleColumns'
    },
    {
      name: 'preferences.endpoint-preferences.sortField',
      type: 'dropdown',
      options: [],
      field: 'machinePreference.sortField'
    }
  ],
  defaultPreferences: {
    machinePreference: {
      visibleColumns: ['machine.machineOsType', 'machine.machineName', 'machine.scanStartTime', 'machine.users.name',
        'agentStatus.lastSeenTime', 'agentStatus.scanStatus'],
      sortField: 'machine.scanStartTime'
    }
  }
};