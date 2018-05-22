export default {
  subscriptionDestination: '/user/queue/endpoint/supported/features',
  requestDestination: '/ws/endpoint/supported/features',
  message() {
    return {
      code: 0,
      data: {
        'rsa.endpoint.fusion': true
      }
    };
  }
};
