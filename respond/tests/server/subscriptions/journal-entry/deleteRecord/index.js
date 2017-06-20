export default {
  subscriptionDestination: '/user/queue/journal/delete',
  requestDestination: '/ws/response/journal/delete',
  message(/* frame */) {
    return {
      data: true
    };
  }
};