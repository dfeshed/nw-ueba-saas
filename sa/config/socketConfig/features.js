
var determineSocketUrl = require('../../../common').determineSocketUrl;

module.exports = function(environment) {

  var endpointSocketUrl = determineSocketUrl(environment, '/endpoint/socket');

  return {
    endpointFeatures: {
      socketUrl: endpointSocketUrl,
      getSupportedFeatures: {
        subscriptionDestination: '/user/queue/endpoint/supported/features',
        requestDestination: '/ws/endpoint/supported/features'
      }
    }
  };
};
