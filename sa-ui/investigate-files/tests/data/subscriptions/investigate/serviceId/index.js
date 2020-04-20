export default {
  subscriptionDestination: '/user/queue/endpoint/investigate/service-id',
  requestDestination: '/ws/endpoint/investigate/service-id',
  message(/* frame */) {
    return {
      data: '1232323123',
      meta: {
        complete: true
      }
    };
  }
};
