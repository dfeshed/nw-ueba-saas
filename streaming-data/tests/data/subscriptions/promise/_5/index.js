export default {
  subscriptionDestination: '/test/subscription/promise/_5',
  requestDestination: '/test/request/promise/_5',
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};