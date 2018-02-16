export default {
  subscriptionDestination: '/user/queue/remediation/tasks/delete',
  requestDestination: '/ws/respond/remediation/tasks/delete',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      data: body.filter[0].values ? body.filter[0].values : [body.filter[0].value]
    };
  }
};