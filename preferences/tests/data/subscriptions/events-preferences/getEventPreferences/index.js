export default {
  subscriptionDestination: '/user/queue/investigate/preferences/get',
  requestDestination: '/ws/investigate/preferences/get',
  count: 0,
  message(/* frame */) {
    const data = {
      userPreferences: {
        defaultLogFormat: 'LOG',
        defaultLandingPage: '',
        defaultPacketFormat: 'PCAP',
        queryTimeFormat: 'DB'
      },
      userServicePreferences: {
        serviceId: 'TestServiceId',
        collectionName: 'Test',
        eventsPreferences: {
          currentReconView: 'TEXT',
          isHeaderOpen: true,
          isMetaShown: true,
          isReconExpanded: true,
          isReconOpen: true,
          isRequestShown: true,
          isResponseShown: true
        }
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
