export default {
  subscriptionDestination: '/user/queue/investigate/predicate/save',
  requestDestination: '/ws/investigate/predicate/save',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      // this would be a hash ID for the given params
      data: '1234'
    };
  }
};
