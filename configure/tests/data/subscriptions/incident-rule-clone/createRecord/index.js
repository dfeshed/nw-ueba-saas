export default {
  subscriptionDestination: '/user/queue/alertrules/clone',
  requestDestination: '/ws/respond/alertrules/clone',
  message(/* frame */) {
    return {
      data: {
        id: '73782378'
      }
    };
  }
};