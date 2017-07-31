export default {
  subscriptionDestination: '/test/subscription/promise/_3',
  requestDestination: '/test/request/promise/_3',
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};