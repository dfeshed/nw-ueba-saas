export default {
  subscriptionDestination: '/user/queue/endpoint/rar/status/set',
  requestDestination: '/ws/endpoint/rar/status/set',
  message(/* frame */) {
    return {
      data: { enabled: true },
      meta: {
        complete: true
      }
    };
  }
};
