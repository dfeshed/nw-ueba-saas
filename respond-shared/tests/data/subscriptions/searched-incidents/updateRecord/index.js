export default {
  delay: 1,
  subscriptionDestination: '/user/queue/investigate/events/incident/update',
  requestDestination: '/ws/investigate/events/incident/update',
  message(/* frame */) {
    return {
      code: 0
    };
  }
};
