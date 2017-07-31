const data = [
  'NEW',
  'ASSIGNED',
  'IN_PROGRESS',
  'REMEDIATION_REQUESTED',
  'REMEDIATION_COMPLETE',
  'CLOSED',
  'CLOSED_FALSE_POSITIVE'
];

export default {
  subscriptionDestination: '/user/queue/options/status',
  requestDestination: '/ws/respond/options/status',
  message(/* frame */) {
    return {
      data,
      meta: {
        total: data.length
      }
    };
  }
};
