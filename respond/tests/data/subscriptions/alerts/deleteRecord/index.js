export default {
  subscriptionDestination: '/user/queue/alerts/delete',
  requestDestination: '/ws/respond/alerts/delete',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      data: body.filter[0].values ? body.filter[0].values : [body.filter[0].value]
    };
  }
};