/**
 * This config will help preferences details to render required preferences.
 *
 * There are four parts in config.
 *
 * modelName:: Determine which socket to fetch or save preferences.
 * addtionalFilterKey:: This property provide flexibility to send query data back for server API.
 * items: Preferences to display along with field details to pull field value from data json.
 * defaultPreferences: In case data is not pulled from server or first time preferences not available.
 * @public
 **/
export default {
  modelName: 'investigate-events-preferences',
  additionalFilterKey: 'userServicePreferences.serviceId',
  helpIds: {
    moduleId: 'investigate',
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
    field: 'eventAnalysisPreferences.currentReconView'
  },
  {
    name: 'preferences.investigate-events.defaultLogFormat',
    type: 'dropdown',
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
    options: [
      'PCAP',
      'PAYLOAD',
      'PAYLOAD1',
      'PAYLOAD2'
    ],
    field: 'eventAnalysisPreferences.defaultPacketFormat'
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
  }],
  defaultPreferences: {
    queryTimeFormat: 'DB',
    eventAnalysisPreferences: {
      currentReconView: 'TEXT',
      isHeaderOpen: true,
      isMetaShown: true,
      isReconExpanded: true,
      isReconOpen: true,
      isRequestShown: true,
      isResponseShown: true,
      defaultLogFormat: 'LOG',
      defaultPacketFormat: 'PCAP',
      autoDownloadExtractedFiles: true
    }
  }
};
