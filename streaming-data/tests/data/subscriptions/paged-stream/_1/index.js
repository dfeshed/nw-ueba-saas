export default {
  subscriptionDestination: '/test/subscription/paged-stream/_1',
  requestDestination: '/test/request/paged-stream/_1',
  page(frame, sendMessage) {
    sendMessage({
      meta: {
        complete: true
      }
    });
  }
};