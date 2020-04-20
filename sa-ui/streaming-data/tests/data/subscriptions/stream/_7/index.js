export default {
  subscriptionDestination: '/test/subscription/stream/_7',
  requestDestination: '/test/request/stream/_7',
  page(frame, sendMessage) {
    // do nothing waiting to trigger a timeout
    setTimeout(function() {
      sendMessage({
        data: [1, 2, 3],
        meta: {
          complete: true
        }
      });
    }, 1500);
  }
};

