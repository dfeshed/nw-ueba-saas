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
        subscriptionDestination: '/user/queue/endpoint/data/files/export',
        requestDestination: '/ws/endpoint/data/files/export'
      },
      saveFilter: {
        subscriptionDestination: '/user/queue/endpoint/data/filter/set',
        requestDestination: '/ws/endpoint/data/filter/set'
      },
      getFilter: {
        subscriptionDestination: '/user/queue/endpoint/data/filter/getall',
        requestDestination: '/ws/endpoint/data/filter/getall'
      },
      deleteFilter: {
        subscriptionDestination: '/user/queue/endpoint/data/filter/remove',
        requestDestination: '/ws/endpoint/data/filter/remove'
      }
    }
  };
};
