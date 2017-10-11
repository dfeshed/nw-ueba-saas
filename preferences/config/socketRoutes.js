/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const investigateSocketUrl = common.determineSocketUrl(environment, '/investigate/socket');

  return {
    'investigate-preferences': {
      socketUrl: investigateSocketUrl,
      getPreferences: {
        subscriptionDestination: '/user/queue/investigate/preferences/get',
        requestDestination: '/ws/investigate/preferences/get'
      },
      setPreferences: {
        subscriptionDestination: '/user/queue/investigate/preferences/set',
        requestDestination: '/ws/investigate/preferences/set'
      }
    }
  };
};
