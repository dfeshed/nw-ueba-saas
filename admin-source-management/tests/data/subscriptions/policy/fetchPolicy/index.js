import policies from '../fetchPolicies/data';

export default {
  subscriptionDestination: '/user/queue/usm/policy/get',
  requestDestination: '/ws/usm/policy/get',
  message(frame) {
    const body = JSON.parse(frame.body);
    let policy = {};
    for (let index = 0; index < policies.length; index++) {
      if (policies[index].id === body.data) {
        policy = policies[index];
        break;
      }
    }
    return {
      code: 0,
      data: policy
    };
  }
};
