export default {
  subscriptionDestination: '/user/queue/usm/policies/publish',
  requestDestination: '/ws/usm/policies/publish',
  message(/* frame */) {
    return {
      code: 0,
      data: 'publish-policies-works'
    };
  }
};