export default {
  subscriptionDestination: '/user/queue/administration/context/liveconnect/feedback',
  requestDestination: '/ws/administration/context/liveconnect/feedback',
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
