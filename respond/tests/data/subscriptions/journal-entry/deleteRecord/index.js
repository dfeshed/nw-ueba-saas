export default {
  subscriptionDestination: '/user/queue/journal/delete',
  requestDestination: '/ws/respond/journal/delete',
  message(/* frame */) {
    return {
      data: true
    };
  }
};