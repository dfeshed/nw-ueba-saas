/* eslint-env node */
const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    packager: {
      socketUrl,
      get: {
        subscriptionDestination: '/user/queue/endpoint/packager/get',
        requestDestination: '/ws/endpoint/packager/get'
      },
      set: {
        subscriptionDestination: '/user/queue/endpoint/packager/set',
        requestDestination: '/ws/endpoint/packager/set'
      },
      create: {
        subscriptionDestination: '/user/queue/endpoint/management/packageconfig/create',
        requestDestination: '/ws/endpoint/management/packageconfig/create'
      }
    }
  };
};
