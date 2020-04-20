export default {
  subscriptionDestination: '/test/subscription/promise/_2',
  requestDestination: '/test/request/promise/_2',
  delay: 1000,
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};