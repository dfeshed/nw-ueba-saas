export default {
  subscriptionDestination: '/user/queue/incidents/delete',
  requestDestination: '/ws/respond/incidents/delete',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      data: body.filter[0].values ? body.filter[0].values : [body.filter[0].value]
    };
  }
};