const data = [
  'LOW',
  'MEDIUM',
  'HIGH',
  'CRITICAL'
];

export default {
  subscriptionDestination: '/user/queue/options/priority',
  requestDestination: '/ws/respond/options/priority',
  message(/* frame */) {
    return {
      data,
      meta: {
        total: data.length
      }
    };
  }
};
