export default {
  subscriptionDestination: '/user/queue/endpoint/preferences/set',
  requestDestination: '/ws/endpoint/preferences/set',
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