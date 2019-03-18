/* eslint-env node */
const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    'endpoint-server-ping': {
      socketUrl
    },
    'endpoint-rar': {
      socketUrl,
      rarInstaller: {
        subscriptionDestination: '/user/queue/endpoint/rar/installer/create',
        requestDestination: '/ws/endpoint/rar/installer/create'
      }
    }
  };
};
