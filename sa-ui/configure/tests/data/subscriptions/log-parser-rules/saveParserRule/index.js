export default {
  subscriptionDestination: '/user/queue/content/parser/rules/update',
  requestDestination: '/ws/content/parser/rules/update',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      id: body.ruleId
    };
  }
};