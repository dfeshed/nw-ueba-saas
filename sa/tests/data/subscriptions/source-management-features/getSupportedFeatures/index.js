export default {
  subscriptionDestination: '/user/queue/usm/supported/features',
  requestDestination: '/ws/usm/supported/features',
  message() {
    return {
      code: 0,
      data: {
        'rsa.usm.allowFilePolicyCreation': true,
        'rsa.usm.viewSources': false
      }
    };
  }
};
