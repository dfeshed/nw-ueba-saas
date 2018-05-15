/* eslint-env node */

const common = require('../../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/administration/socket');

  return {
    'context-service': {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/administration/context/lookup',
        requestDestination: '/ws/administration/context/lookup',
        cancelDestination: '/ws/administration/context/cancel'
      }
    }
  };
};