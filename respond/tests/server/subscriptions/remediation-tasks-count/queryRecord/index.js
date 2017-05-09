export default {
  subscriptionDestination: '/user/queue/remediation/tasks/count',
  requestDestination: '/ws/response/remediation/tasks/count',
  message(/* frame */) {
    return {
      data: 1,
      meta: {
        total: 6
      }
    };
  }
};