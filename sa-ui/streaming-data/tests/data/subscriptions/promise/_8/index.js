export default {
  subscriptionDestination: '/test/subscription/promise/_8',
  requestDestination: '/test/request/promise/_8',
  delay: 1500,
  message() {
    return {
      code: 456
    };
  }
};