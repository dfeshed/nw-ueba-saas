export default {
  subscriptionDestination: '/user/queue/usm/groups/remove',
  requestDestination: '/ws/usm/groups/remove',
  message(/* frame */) {
    return {
      code: 0,
      data: 'delete-works'
    };
  }
};