export default {
  subscriptionDestination: '/test/subscription/stream/_12',
  requestDestination: '/test/request/stream/_12',
  cancelDestination: '/test/request/stream/_12',
  page(frame, sendMessage) {
    sendMessage({
      data: [],
      meta: {
      }
    });
  }
};

