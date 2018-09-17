export default {
  subscriptionDestination: '/user/queue/contexthub/certificate/status/set',
  requestDestination: '/ws/contexthub/certificate/status/set',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data: {
        statusCode: 'OK'
      }
    };
  }
};
