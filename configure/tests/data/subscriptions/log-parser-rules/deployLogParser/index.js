export default {
  subscriptionDestination: '/user/queue/content/parser/deploy',
  requestDestination: '/ws/content/parser/deploy',
  message(frame) {
    const body = JSON.parse(frame.body);
    return {
      code: 0,
      data: body.ruleId
    };
  }
};