export default {
  subscriptionDestination: '/user/queue/usm/sources/publish',
  requestDestination: '/ws/usm/sources/publish',
  message(/* frame */) {
    return {
      code: 0,
      data: 'publish-sources-works'
    };
  }
};