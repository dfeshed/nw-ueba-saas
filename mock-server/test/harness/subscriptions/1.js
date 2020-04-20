export default {
  subscriptionDestination: '/test/subscription/_1',
  requestDestination: '/test/request/_1',
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};