export default {
  subscriptionDestination: '/test/subscription/promise/_6',
  requestDestination: '/test/request/promise/_6',
  delay: 500,
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};