export default {
  subscriptionDestination: '/user/queue/endpoint/file/export',
  requestDestination: '/ws/endpoint/file/export',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data: {
        statusCode: 'OK',
        id: '123'
      }
    };
  }
};