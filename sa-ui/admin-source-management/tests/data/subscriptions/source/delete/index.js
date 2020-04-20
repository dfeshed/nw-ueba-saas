export default {
  subscriptionDestination: '/user/queue/usm/sources/remove',
  requestDestination: '/ws/usm/sources/remove',
  message(/* frame */) {
    return {
      code: 0,
      data: 'delete-sources-works'
    };
  }
};