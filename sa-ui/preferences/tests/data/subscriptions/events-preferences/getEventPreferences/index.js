export default {
  subscriptionDestination: '/user/queue/investigate/preferences/get',
  requestDestination: '/ws/investigate/preferences/get',
  count: 0,
  delay: 1,
  message(/* frame */) {
    const data = {
      queryTimeFormat: 'DB',
      eventAnalysisPreferences: {
        currentReconView: 'PACKET',
        isHeaderOpen: true,
        isMetaShown: true,
        isReconExpanded: true,
        isReconOpen: true,
        isRequestShown: true,
        isResponseShown: true,
        defaultLogFormat: 'TEXT',
        defaultPacketFormat: 'PCAP',
        defaultMetaFormat: 'TEXT',
        autoDownloadExtractedFiles: true,
        packetsPageSize: 100,
        autoUpdateSummary: false,
        eventTimeSortOrder: 'Unsorted'
      },
      eventPreferences: {
        columnGroup: 'EMAIL'
      }
    };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
