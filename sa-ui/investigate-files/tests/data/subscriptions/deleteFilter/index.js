export default {
  subscriptionDestination: '/user/queue/endpoint/filter/remove',
  requestDestination: '/ws/endpoint/filter/remove',
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
