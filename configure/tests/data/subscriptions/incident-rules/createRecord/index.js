export default {
  subscriptionDestination: '/user/queue/alertrules/create',
  requestDestination: '/ws/respond/alertrules/create',
  message(/* frame */) {
    return {
      data: {
        id: '73782378'
      }
    };
  }
};