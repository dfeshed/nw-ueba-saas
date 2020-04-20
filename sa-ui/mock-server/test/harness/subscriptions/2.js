export default {
  subscriptionDestination: '/test/subscription/_2',
  requestDestination: '/test/request/_2',
  page(dontcare, sendMessage) {
    sendMessage({
      data: [2, 2, 2, 2, 2]
    });
  }
};