export default {
  subscriptionDestination: '/test/subscription/stream/_8',
  requestDestination: '/test/request/stream/_8',
  page(frame, sendMessage) {
    // do nothing waiting to trigger a timeout
    setTimeout(function() {
      sendMessage({
        code: 1234,
        meta: {
          complete: true
        }
      });
    }, 1500);
  }
};

