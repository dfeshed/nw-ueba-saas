
var determineSocketUrl = require('../../../common').determineSocketUrl;

module.exports = function(environment) {

  var endpointSocketUrl = determineSocketUrl(environment, '/endpoint/socket');
  var usmSocketUrl = determineSocketUrl(environment, '/usm/socket');

  return {
    endpointFeatures: {
      socketUrl: endpointSocketUrl,
      getSupportedFeatures: {
        subscriptionDestination: '/user/queue/endpoint/supported/features',
        requestDestination: '/ws/endpoint/supported/features'
      }
    },
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
