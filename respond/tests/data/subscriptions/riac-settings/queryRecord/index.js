export default {
  delay: 1,
  subscriptionDestination: '/user/queue/incident/access/control/settings',
  requestDestination: '/ws/respond/incident/access/control/settings',
  message() {
    return {
      data: {
        enabled: false
      }
    };
  }
};
