/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const investigateSocketUrl = common.determineSocketUrl(environment, '/investigate/socket');

  return {
    'investigate-preferences': {
      investigateSocketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/investigate/preferences/set',
        requestDestination: '/ws/investigate/preferences/set'
      },
      queryRecord: {
        subscriptionDestination: '/user/queue/investigate/preferences/get',
        requestDestination: '/ws/investigate/preferences/get'
      }
    }
  };
};
