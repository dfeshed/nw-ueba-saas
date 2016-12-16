var determineSocketUrl = require('../../../common').determineSocketUrl;

module.exports = function(environment) {

  var socketUrl = determineSocketUrl(environment, '/administration/socket');

  // remove this line when mock server in place
  socketUrl = '/administration/socket';

  return {
    preferences: {
      socketUrl,
      getPreference: {
        subscriptionDestination: '/user/queue/administration/global/get/user/preferences',
        requestDestination: '/ws/administration/global/get/user/preferences'
      },
      setPreference: {
        subscriptionDestination: '/user/queue/administration/global/set/user/preferences',
        requestDestination: '/ws/administration/global/set/user/preferences'
      }
    },
    timezones: {
      socketUrl,
      getTimezones: {
        subscriptionDestination: '/user/queue/administration/timezones/get',
        requestDestination: '/ws/administration/timezones/get'
      }
    }

  };
};
