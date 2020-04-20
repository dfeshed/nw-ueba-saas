import data from '../../incident-rules/findAll/data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/alertrules/reorder',
  requestDestination: '/ws/respond/alertrules/reorder',
  message(frame) {
    const body = JSON.parse(frame.body);
    const reorderedIds = body.data;
    const reorderedRules = reorderedIds.map((ruleId, index) => {
      let rule = data.find((rule) => rule.id === ruleId);
      if (!rule) {
        rule = {
          ruleId
        };
      }
      rule.order = index + 1;
      return rule;
    });
    return {
      code: 0,
      data: reorderedRules
    };
  }
};
