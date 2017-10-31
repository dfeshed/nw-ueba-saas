/* eslint-env node */
// THE FOLLOWING IS AN EXAMPLE, WILL NEED TO BE CHANGED PER ENGINE

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    'files': {
      socketUrl,
      'schema': {
        subscriptionDestination: '/user/queue/endpoint/data/files/schema',
        requestDestination: '/ws/endpoint/data/files/schema'
      },
      search: {
        subscriptionDestination: '/user/queue/endpoint/data/files/search',
        requestDestination: '/ws/endpoint/data/files/search'
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
      }
    }
  };
};
