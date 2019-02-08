export default {
  delay: 1,
  subscriptionDestination: '/user/queue/journal/update',
  requestDestination: '/ws/respond/journal/update',
  message(/* frame */) {
    return {
      data: true
    };
  }
};
