export default {
  subscriptionDestination: '/user/queue/administration/features',
  requestDestination: '/ws/administration/features',
  message(/* frame */) {
    return {
      data: { contextHubEnabled: true }
    };
  }
};