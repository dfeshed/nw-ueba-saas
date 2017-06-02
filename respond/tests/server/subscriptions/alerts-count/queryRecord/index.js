export default {
  subscriptionDestination: '/user/queue/alerts/count',
  requestDestination: '/ws/response/alerts/count',
  message(/* frame */) {
    return {
      data: 10,
      meta: {
        total: 10
      }
    };
  }
};