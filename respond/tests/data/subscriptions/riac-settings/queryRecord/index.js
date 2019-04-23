export default {
  delay: 1,
  subscriptionDestination: '/user/queue/riac',
  requestDestination: '/ws/respond/incident/access/control/settings',
  message() {
    return {
      data: {
        enabled: false
      }
    };
  }
};
