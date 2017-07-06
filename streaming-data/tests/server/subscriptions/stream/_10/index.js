export default {
  subscriptionDestination: '/test/subscription/stream/_10',
  requestDestination: '/test/request/stream/_10',
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

