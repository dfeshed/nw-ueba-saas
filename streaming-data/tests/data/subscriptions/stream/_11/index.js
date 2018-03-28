export default {
  subscriptionDestination: '/test/subscription/stream/_11',
  requestDestination: '/test/request/stream/_11',
  page(frame, sendMessage) {
    sendMessage({
      code: 1234,
      meta: {
        complete: true
      }
    });
  }
};

