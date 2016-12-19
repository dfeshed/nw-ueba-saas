export default {
  subscriptionDestination: '/test/subscription/stream/_6',
  requestDestination: '/test/request/stream/_6',
  page(frame, sendMessage) {
    // do nothing waiting to trigger a timeout
    setTimeout(function() {
      sendMessage({
        data: [1, 2, 3],
        meta: {
          complete: true
        }
      });
    }, 500);
  }
};

