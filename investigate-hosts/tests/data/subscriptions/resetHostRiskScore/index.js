export default {
  subscriptionDestination: '/user/queue/risk/context/host/reset',
  requestDestination: '/ws/respond/risk/context/host/reset',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      }
    };
  }
};
