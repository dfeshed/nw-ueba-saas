export default {
  subscriptionDestination: '/user/queue/alertrules/delete',
  requestDestination: '/ws/respond/alertrules/delete',
  message(/* frame */) {
    return {
      data: {}
    };
  }
};