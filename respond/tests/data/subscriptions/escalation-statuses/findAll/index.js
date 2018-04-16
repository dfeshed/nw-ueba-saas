const data = [
  'Remediated'
];

export default {
  subscriptionDestination: '/user/queue/incidents/escalationStatuses',
  requestDestination: '/ws/respond/incidents/escalationStatuses',
  message(/* frame */) {
    return {
      data,
      meta: {
        total: data.length
      }
    };
  }
};
