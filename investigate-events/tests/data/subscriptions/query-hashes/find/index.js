export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get',
  requestDestination: '/ws/investigate/predicate/get',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      // TODO: flesh this out if frame has hashes, provide some
      // random pill data
      params: []
    };
  }
};

