/**
 * This config will help preferences details to render required preferences.
 *
 * There are four parts in config.
 *
 * modelName:: Determine which socket to fetch or save preferences.
 * fieldPrefix:: This is prefix used for proper translation of items.options. Used in preferences/preferences-details
 *   Ex: For options 'TEXT' to be properly translated, make sure preferences.investigate-events.TEXT (fieldPrefix+option)
 *   is present in translation file
 * addtionalFilterKey:: This property provide flexibility to send query data back for server API.
 * items: Preferences to display along with field details to pull field value from data json.
 * defaultPreferences: In case data is not pulled from server or first time preferences not available.
 * @public
 **/
export default {
  modelName: 'investigate-events-preferences',
  fieldPrefix: 'preferences.investigate-events',
  additionalFilterKey: 'userServicePreferences.serviceId',
  helpIds: {
    moduleId: 'investigation',
    topicId: 'investigateEventPreferences'
  },
  items: [{
    name: 'preferences.investigate-events.defaultEventView',
    type: 'dropdown',
    options: [
      'TEXT',
      'PACKET',
      'FILE'
    ],
    additionalFieldPrefix: 'reconView',
    field: 'eventAnalysisPreferences.currentReconView'
  },
  {
    name: 'preferences.investigate-events.defaultLogFormat',
    type: 'dropdown',
    eventType: 'LOG',
    options: [
      'LOG',
      'CSV',
      'XML',
      'JSON'
    ],
    field: 'eventAnalysisPreferences.defaultLogFormat'
  },
  {
    name: 'preferences.investigate-events.defaultPacketFormat',
    type: 'dropdown',
    eventType: 'NETWORK',
    options: [
      'PCAP',
      'PAYLOAD',
      'PAYLOAD1',
      'PAYLOAD2'
    ],
    field: 'eventAnalysisPreferences.defaultPacketFormat'
  },
  {
    name: 'preferences.investigate-events.defaultMetaFormat',
    type: 'dropdown',
    eventType: 'META',
    options: [
      'TEXT',
      'CSV',
      'TSV',
      'JSON'
    ],
    field: 'eventAnalysisPreferences.defaultMetaFormat'
  },
  {
    name: 'preferences.investigate-events.queryTimeFormat',
    type: 'radio',
    options: [
      'DB',
      'WALL'
    ],
    field: 'queryTimeFormat'
  },
  {
    name: 'preferences.investigate-events.autoDownloadExtractedFiles',
    type: 'checkbox',
    field: 'eventAnalysisPreferences.autoDownloadExtractedFiles'
  },
  {
    name: 'preferences.investigate-events.autoUpdateSummary',
    type: 'switch',
    field: 'eventAnalysisPreferences.autoUpdateSummary'
  }],
  defaultPreferences: {
    queryTimeFormat: 'DB',
    eventAnalysisPreferences: {
      currentReconView: 'TEXT',
      defaultLogFormat: 'LOG',
      defaultPacketFormat: 'PCAP',
      defaultMetaFormat: 'TEXT',
      autoDownloadExtractedFiles: true,
      packetsPageSize: 100,
      autoUpdateSummary: false
    },
    eventPreferences: {
      columnGroup: 'SUMMARY'
    }
  }
};
