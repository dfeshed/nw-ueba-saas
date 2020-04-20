export default {
  subscriptionDestination: '/test/subscription/promise/_7',
  requestDestination: '/test/request/promise/_7',
  delay: 1500,
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};