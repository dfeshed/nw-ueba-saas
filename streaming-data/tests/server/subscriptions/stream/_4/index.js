export default {
  subscriptionDestination: '/test/subscription/stream/_4',
  requestDestination: '/test/request/stream/_4',
  page(frame, sendMessage) {
    sendMessage({
      code: 100
    });
  }
};