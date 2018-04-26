const data = {
  isArcherDataSourceConfigured: false
};

export default {
  subscriptionDestination: '/user/queue/incidents/escalation/configuration',
  requestDestination: '/ws/respond/incidents/escalation/configuration',
  message(/* frame */) {
    return {
      data
    };
  }
};
