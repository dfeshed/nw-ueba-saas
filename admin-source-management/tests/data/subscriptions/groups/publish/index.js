export default {
  subscriptionDestination: '/user/queue/usm/groups/publish',
  requestDestination: '/ws/usm/groups/publish',
  message(/* frame */) {
    return {
      code: 0,
      data: 'publish-works'
    };
  }
};