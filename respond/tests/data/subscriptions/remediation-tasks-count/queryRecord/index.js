export default {
  subscriptionDestination: '/user/queue/remediation/tasks/count',
  requestDestination: '/ws/respond/remediation/tasks/count',
  message(/* frame */) {
    return {
      data: 1,
      meta: {
        total: 6
      }
    };
  }
};