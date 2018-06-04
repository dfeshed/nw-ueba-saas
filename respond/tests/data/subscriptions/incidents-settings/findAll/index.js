const data = {
  isArcherDataSourceConfigured: true
};

export default {
  subscriptionDestination: '/user/queue/incidents/archer/configuration',
  requestDestination: '/ws/respond/incidents/archer/configuration',
  message(/* frame */) {
    return {
      data
    };
  }
};
