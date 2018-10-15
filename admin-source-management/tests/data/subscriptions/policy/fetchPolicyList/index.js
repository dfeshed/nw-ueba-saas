import policies from '../fetchPolicies/data';

export default {
  subscriptionDestination: '/user/queue/usm/policies/list',
  requestDestination: '/ws/usm/policies/list',
  message(/* frame */) {
    const list = policies.map(function(policy) {
      return {
        id: policy.id,
        name: policy.name,
        policyType: policy.policyType,
        defaultPolicy: policy.defaultPolicy,
        description: policy.description,
        createdOn: policy.createdOn,
        lastModifiedOn: policy.lastModifiedOn,
        lastPublishedOn: policy.lastPublishedOn,
        dirty: policy.dirty
      };
    });
    return {
      code: 0,
      data: list
    };
  }
};
