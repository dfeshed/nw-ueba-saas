export default {
  subscriptionDestination: '/test/subscription/promise/_10',
  requestDestination: '/test/request/promise/_10',
  delay: 1500,
  message() {
    return {
      code: 456
    };
  }
};