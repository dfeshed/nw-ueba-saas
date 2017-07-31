export default {
  subscriptionDestination: '/user/queue/incidents/count',
  requestDestination: '/ws/respond/incidents/count',
  message(/* frame */) {
    return {
      data: 1,
      meta: {
        total: 41
      }
    };
  }
};