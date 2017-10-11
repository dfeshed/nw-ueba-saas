export default {
  subscriptionDestination: '/user/queue/investigate/preferences/get',
  requestDestination: '/ws/investigate/preferences/get',
  count: 0,
  message(/* frame */) {
    const data = { code: 0, defaultViewType: 'text' };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
