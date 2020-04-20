export default {
  subscriptionDestination: '/user/queue/endpoint/data/filter/remove',
  requestDestination: '/ws/endpoint/data/filter/remove',
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
