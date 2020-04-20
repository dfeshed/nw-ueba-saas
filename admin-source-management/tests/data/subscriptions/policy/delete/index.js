export default {
  subscriptionDestination: '/user/queue/usm/policies/remove',
  requestDestination: '/ws/usm/policies/remove',
  message(/* frame */) {
    return {
      code: 0,
      data: 'delete-policies-works'
    };
  }
};