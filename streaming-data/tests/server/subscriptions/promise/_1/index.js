export default {
  subscriptionDestination: '/test/subscription/promise/_1',
  requestDestination: '/test/request/promise/_1',
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};