export default {
  subscriptionDestination: '/queue/incidents/update',
  requestDestination: '/ws/response/incidents/update',
  message(data) {
    return {
      data
    };
  }
};