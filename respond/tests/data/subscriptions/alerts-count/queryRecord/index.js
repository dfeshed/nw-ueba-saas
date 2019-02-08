export default {
  delay: 1,
  subscriptionDestination: '/user/queue/alerts/count',
  requestDestination: '/ws/respond/alerts/count',
  message(/* frame */) {
    return {
      data: 10,
      meta: {
        total: 10
      }
    };
  }
};
