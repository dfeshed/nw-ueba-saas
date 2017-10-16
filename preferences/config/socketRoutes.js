/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/investigate/socket');

  return {
    'events-preferences': {
      socketUrl,
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
