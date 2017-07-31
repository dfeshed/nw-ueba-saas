export default {
  subscriptionDestination: '/test/subscription/stream/_1',
  requestDestination: '/test/request/stream/_1',
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};