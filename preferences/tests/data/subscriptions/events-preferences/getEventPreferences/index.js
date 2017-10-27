export default {
  subscriptionDestination: '/user/queue/investigate/preferences/get',
  requestDestination: '/ws/investigate/preferences/get',
  count: 0,
  message(/* frame */) {
    const data = { eventsPreferences: { code: 0, defaultAnalysisView: 'packet', isMetaShown: null, isHeaderOpen: null } };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
