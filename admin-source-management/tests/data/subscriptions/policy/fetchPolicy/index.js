import policies from '../fetchPolicies/data';

export default {
  subscriptionDestination: '/user/queue/usm/policy/get',
  requestDestination: '/ws/usm/policy/get',
  message(frame) {
    const body = JSON.parse(frame.body);
    let policy = {};
    let returnCode = -1; // use the real error code if we ever add one for "Policy does NOT exist"
    for (let index = 0; index < policies.length; index++) {
      if (policies[index].id === body.data) {
        policy = policies[index];
        returnCode = 0;
        break;
      }
    }
    return {
      code: returnCode, // 0 == Ok, anything else == Error
      data: policy // change this to error info if we ever add anything for "Policy does NOT exist"
    };
  }
};
