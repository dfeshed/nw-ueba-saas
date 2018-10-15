export default {
  subscriptionDestination: '/user/queue/risk/score/file/context/reset',
  requestDestination: '/ws/risk/score/file/context/reset',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      }
    };
  }
};