export default {
  subscriptionDestination: '/user/queue/risk/context/file/reset',
  requestDestination: '/ws/respond/risk/context/file/reset',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      }
    };
  }
};
