
var determineSocketUrl = require('../../../common').determineSocketUrl;

module.exports = function(environment) {

  var usmSocketUrl = determineSocketUrl(environment, '/usm/socket');

  return {
    // source management (a.k.a. USM)
    sourceManagementFeatures: {
      socketUrl: usmSocketUrl,
      getSupportedFeatures: {
        subscriptionDestination: '/user/queue/usm/supported/features',
        requestDestination: '/ws/usm/supported/features'
      }
    }
  };
};
