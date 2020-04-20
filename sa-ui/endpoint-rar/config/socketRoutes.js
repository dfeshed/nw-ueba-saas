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
      },
      get: {
        subscriptionDestination: '/user/queue/endpoint/rar/config/get',
        requestDestination: '/ws/endpoint/rar/config/get'
      },
      set: {
        subscriptionDestination: '/user/queue/endpoint/rar/config/set',
        requestDestination: '/ws/endpoint/rar/config/set'
      },
      testConfig: {
        subscriptionDestination: '/user/queue/endpoint/rar/test',
        requestDestination: '/ws/endpoint/rar/test'
      },
      getEnableStatus: {
        subscriptionDestination: '/user/queue/endpoint/rar/status/get',
        requestDestination: '/ws/endpoint/rar/status/get'
      },
      setEnableStatus: {
        subscriptionDestination: '/user/queue/endpoint/rar/status/set',
        requestDestination: '/ws/endpoint/rar/status/set'
      }
    }
  };
};
