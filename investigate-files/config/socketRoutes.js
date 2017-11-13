/* eslint-env node */
// THE FOLLOWING IS AN EXAMPLE, WILL NEED TO BE CHANGED PER ENGINE

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    'files': {
      socketUrl,
      'schema': {
        subscriptionDestination: '/user/queue/endpoint/file/schema',
        requestDestination: '/ws/endpoint/file/schema'
      },
      search: {
        subscriptionDestination: '/user/queue/endpoint/file/search',
        requestDestination: '/ws/endpoint/file/search'
      },
      exportFile: {
        subscriptionDestination: '/user/queue/endpoint/file/property/download',
        requestDestination: '/ws/endpoint/file/property/download'
      },
      saveFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/set',
        requestDestination: '/ws/endpoint/filter/set'
      },
      getFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/get-all',
        requestDestination: '/ws/endpoint/filter/get-all'
      },
      deleteFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/remove',
        requestDestination: '/ws/endpoint/filter/remove'
      }
    }
  };
};
