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
      }
    }
  };
};
