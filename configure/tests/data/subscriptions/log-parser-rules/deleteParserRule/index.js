export default {
  subscriptionDestination: '/user/queue/content/parser/update',
  requestDestination: '/ws/content/parser/update',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      id: body.ruleId
    };
  }
};