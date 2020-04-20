export default {
  subscriptionDestination: '/user/queue/investigate/events/countdistinct',
  requestDestination: '/ws/investigate/events/countdistinct',
  message(/* frame */) {
    return {
      data: 1
    };
  }
};
