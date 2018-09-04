export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-by-query',
  requestDestination: '/ws/investigate/predicate/get-by-query',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      // this would be a hash ID for the given params
      data: [{
        id: '1f4',
        query: 'medium=32 && ip.src="123.45.25.24"',
        displayName: 'Cisco Logs',
        createdBy: 'Jay',
        createdOn: 1,
        lastUsed: 1
      }]
    };
  }
};
