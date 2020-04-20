export default {
  subscriptionDestination: '/user/queue/alertrules/enable',
  requestDestination: '/ws/respond/alertrules/enable',
  message(frame) {
    const { data } = JSON.parse(frame.body);
    return {
      data
    };
  }
};