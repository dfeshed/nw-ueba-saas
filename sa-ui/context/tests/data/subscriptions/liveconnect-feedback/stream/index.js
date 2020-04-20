export default {
  subscriptionDestination: '/user/queue/contexthub/context/liveconnect/feedback',
  requestDestination: '/ws/contexthub/context/liveconnect/feedback',
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
