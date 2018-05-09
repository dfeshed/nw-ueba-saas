export default {
  subscriptionDestination: '/user/queue/parser-rules/rule/delete',
  requestDestination: '/ws/logs/parser-rules/rule/delete',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      data: body.ruleId
    };
  }
};
