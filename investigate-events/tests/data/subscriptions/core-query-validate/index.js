export default {
  subscriptionDestination: '/user/queue/investigate/validate/query',
  requestDestination: '/ws/investigate/validate/query',
  message(/* frame */) {
    return {
      code: 0,
      data: true
    };
  }
};