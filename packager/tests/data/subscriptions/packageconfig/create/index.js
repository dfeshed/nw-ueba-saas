export default {
  subscriptionDestination: '/user/queue/endpoint/management/packageconfig/create',
  requestDestination: '/ws/endpoint/management/packageconfig/create',
  message(/* frame */) {
    const now = Number(new Date());
    const link = `/data/agent.zip?datetime=${now}`;
    return {
      meta: {
        complete: true
      },
      data: {
        statusCode: 'OK',
        link
      }
    };
  }
};
