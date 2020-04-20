const data = {
  isArcherDataSourceConfigured: true
};

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/incidents/archer/configuration',
  requestDestination: '/ws/respond/incidents/archer/configuration',
  message(/* frame */) {
    return {
      data
    };
  }
};
