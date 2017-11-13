export default {
  subscriptionDestination: '/user/queue/investigate/preferences/get',
  requestDestination: '/ws/investigate/preferences/get',
  count: 0,
  message(/* frame */) {
    const data = {
      userPreferences: {
        defaultLogFormat: 'XML',
        defaultPacketFormat: 'PAYLOAD1',
        queryTimeFormat: 'DB'
      },
      userServicePreferences: {
        serviceId: 'TestServiceId',
        collectionName: 'Test',
        eventsPreferences: {
          currentReconView: 'PACKET',
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
