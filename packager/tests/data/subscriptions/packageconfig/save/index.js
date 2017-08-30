export default {
  subscriptionDestination: '/user/queue/endpoint/management/packageconfig/save',
  requestDestination: '/ws/endpoint/management/packageconfig/save',
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
