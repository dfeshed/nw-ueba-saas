export default {
  subscriptionDestination: '/test/subscription/_5',
  requestDestination: '/test/request/_5',
  delay: 1000,
  message() {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};