export default {
  subscriptionDestination: '/user/queue/alerts/associate',
  requestDestination: '/ws/respond/alerts/associate',
  message(/* frame */) {
    return {
      code: 0
    };
  }
};