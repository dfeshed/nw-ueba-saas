var determineSocketUrl = require('mock-server/util').determineSocketUrl;

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
    }
  };
}