const data = {
  isArcherDataSourceConfigured: false
};

export default {
  subscriptionDestination: '/user/queue/incidents/configuration',
  requestDestination: '/ws/respond/incidents/configuration',
  message(/* frame */) {
    return {
      data
    };
  }
};
