/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/investigate/socket');
  const endpointSocketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    'investigate-events-preferences': {
      socketUrl,
      getPreferences: {
        subscriptionDestination: '/user/queue/investigate/preferences/get',
        requestDestination: '/ws/investigate/preferences/get'
      },
      setPreferences: {
        subscriptionDestination: '/user/queue/investigate/preferences/set',
        requestDestination: '/ws/investigate/preferences/set'
      }
    },
    'endpoint-preferences': {
      socketUrl: endpointSocketUrl,
      getPreferences: {
        subscriptionDestination: '/user/queue/endpoint/preferences/get',
        requestDestination: '/ws/endpoint/preferences/get'
      },
      setPreferences: {
        subscriptionDestination: '/user/queue/endpoint/preferences/set',
        requestDestination: '/ws/endpoint/preferences/set'
      }
    }
  };
};
