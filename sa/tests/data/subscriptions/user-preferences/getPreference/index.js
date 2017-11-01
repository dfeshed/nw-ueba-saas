export default {
  subscriptionDestination: '/user/queue/administration/global/get/user/preferences',
  requestDestination: '/ws/administration/global/get/user/preferences',
  message() {
    const data = { eventsPreferences: { code: 0, defaultAnalysisView: 'packet', isMetaShown: null, isHeaderOpen: null } };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
