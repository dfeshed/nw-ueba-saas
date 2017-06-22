export default {
  subscriptionDestination: '/queue/incidents/update',
  requestDestination: '/ws/respond/incidents/update',
  message(data) {
    return {
      data
    };
  }
};