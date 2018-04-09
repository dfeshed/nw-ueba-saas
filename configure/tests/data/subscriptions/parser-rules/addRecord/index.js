export default {
  subscriptionDestination: '/user/queue/parser-rules/rule/add',
  requestDestination: '/ws/logs/parser-rules/rule/add',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      data: body.ruleId
    };
  }
};
