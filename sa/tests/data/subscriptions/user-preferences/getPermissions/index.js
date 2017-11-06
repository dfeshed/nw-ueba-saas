export default {
  subscriptionDestination: '/user/queue/administration/rbac/get/permissions',
  requestDestination: '/ws/administration/rbac/get/permissions',
  message() {
    return {
      code: 0,
      data: [
        'viewAppliances',
        'searchLiveResources',
        'accessInvestigationModule',
        'respond-server.*'
      ]
    };
  }
};
