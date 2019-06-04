export default {
  subscriptionDestination: '/user/queue/endpoint/rar/status/get',
  requestDestination: '/ws/endpoint/rar/status/get',
  message(/* frame */) {
    return {
      data: { enabled: true },
      meta: {
        complete: true
      }
    };
  }
};
