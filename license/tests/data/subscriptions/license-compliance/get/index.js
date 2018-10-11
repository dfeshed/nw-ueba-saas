export default {
  subscriptionDestination: '/user/queue/license/compliance/get',
  requestDestination: '/ws/license/compliance/get',
  count: 0,
  message(/* frame */) {
    const data = {
      compliant: false,
      compliances: [
        {
          subjectId: 'subject-1',
          subjectType: 'LICENSE',
          status: 'EXPIRED'
        },
        {
          subjectId: 'subject-2',
          subjectType: 'LICENSE',
          status: 'USAGE_LIMIT_NEARING'
        }
      ]
    };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
