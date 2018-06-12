export default {
  subscriptionDestination: '/user/queue/administration/rbac/get/permissions',
  requestDestination: '/ws/administration/rbac/get/permissions',
  message() {
    return {
      code: 0,
      data: [
        'viewAppliances',
        'viewServices',
        'viewEventSources',
        'viewUnifiedSources',
        'accessHealthWellness',
        'manageSystemSettings',
        'manageSASecurity',
        'searchLiveResources',
        'accessInvestigationModule',
        'respond-server.*',
        'investigate-server.*',
        'integration-server.*',
        'endpoint-server.machine.read'
      ]
    };
  }
};
