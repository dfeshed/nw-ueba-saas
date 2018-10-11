/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/license/socket');

  return {
    'license-compliance': {
      socketUrl,
      get: {
        subscriptionDestination: '/user/queue/license/compliance/get',
        requestDestination: '/ws/license/compliance/get'
      }
    }
  };
};
