export default {
  subscriptionDestination: '/user/queue/alertrules/disable',
  requestDestination: '/ws/respond/alertrules/disable',
  message(frame) {
    const { data } = JSON.parse(frame.body);
    return {
      data
    };
  }
};