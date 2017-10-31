/* eslint-env node */
const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    packager: {
      socketUrl,
      get: {
        subscriptionDestination: '/user/queue/endpoint/management/packageconfig/get',
        requestDestination: '/ws/endpoint/management/packageconfig/get'
      },
      set: {
        subscriptionDestination: '/user/queue/endpoint/management/packageconfig/save',
        requestDestination: '/ws/endpoint/management/packageconfig/save'
      },
      create: {
        subscriptionDestination: '/user/queue/endpoint/management/packageconfig/create',
        requestDestination: '/ws/endpoint/management/packageconfig/create'
      }
    }
  };
};
