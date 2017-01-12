var determineSocketUrl = require('../../../common').determineSocketUrl;

module.exports = function(environment) {

  var socketUrl = determineSocketUrl(environment, '/administration/socket');

  // remove this line when mock server in place
  socketUrl = '/administration/socket';

  return {
    context: {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/administration/context/lookup',
        requestDestination: '/ws/administration/context/lookup',
        cancelDestination: '/ws/administration/context/cancel'
      }
    },
    'related-entity': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/administration/context/liveconnect/related',
        requestDestination: '/ws/administration/context/liveconnect/related'
      }
    }
  };
}