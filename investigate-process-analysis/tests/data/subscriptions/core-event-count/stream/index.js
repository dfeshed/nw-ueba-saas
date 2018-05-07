export default {
  subscriptionDestination: '/user/queue/investigate/events/count',
  requestDestination: '/ws/investigate/events/count',
  message(/* frame */) {
    return {
      data: 1
    };
  }
};
