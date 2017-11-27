export default {
  subscriptionDestination: '/user/queue/endpoint/policy/set',
  requestDestination: '/ws/endpoint/policy/set',
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
