export default {
  subscriptionDestination: '/user/queue/usm/supported/features',
  requestDestination: '/ws/usm/supported/features',
  message() {
    return {
      code: 0,
      data: {
        'rsa.usm.viewSourcesFeature': false,
        'rsa.usm.filePolicyFeature': true,
        'rsa.usm.allowFilePolicies': true
      }
    };
  }
};
