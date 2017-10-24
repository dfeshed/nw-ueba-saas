export default {
  subscriptionDestination: '/user/queue/investigate/preferences/get',
  requestDestination: '/ws/investigate/preferences/get',
  count: 0,
  message(/* frame */) {
    const data = { code: 0, eventsPreferences: { defaultAnalysisView: 'packet' } };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
