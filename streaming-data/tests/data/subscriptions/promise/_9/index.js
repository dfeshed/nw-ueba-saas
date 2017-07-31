export default {
  subscriptionDestination: '/test/subscription/promise/_9',
  requestDestination: '/test/request/promise/_9',
  delay: 1500,
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};