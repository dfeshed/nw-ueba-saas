export default {
  subscriptionDestination: '/user/queue/endpoint/insights-policy/set',
  requestDestination: '/ws/endpoint/insights-policy/set',
  message(/* frame */) {
    return {
      data: {
        success: true
      },
      meta: {
        complete: true
      }
    };
  }
};
