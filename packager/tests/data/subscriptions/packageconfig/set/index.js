export default {
  subscriptionDestination: '/user/queue/endpoint/packager/set',
  requestDestination: '/ws/endpoint/packager/set',
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
