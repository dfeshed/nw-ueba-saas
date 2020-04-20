export default {
  subscriptionDestination: '/test/subscription/promise/_4',
  requestDestination: '/test/request/promise/_4',
  message() {
    // testing error handling, just return code
    return {
      code: 456
    };
  }
};