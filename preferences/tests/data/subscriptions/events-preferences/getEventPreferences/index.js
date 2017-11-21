export default {
  subscriptionDestination: '/user/queue/investigate/preferences/get',
  requestDestination: '/ws/investigate/preferences/get',
  count: 0,
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
        defaultLogFormat: 'LOG',
        defaultPacketFormat: 'PCAP',
        autoDownloadExtractedFiles: true
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
