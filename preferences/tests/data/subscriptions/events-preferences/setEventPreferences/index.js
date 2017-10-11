export default {
  subscriptionDestination: '/user/queue/investigate/preferences/set',
  requestDestination: '/ws/investigate/preferences/set',
  count: 0,
  message(/* frame */) {
    const data = { code: 0 };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
