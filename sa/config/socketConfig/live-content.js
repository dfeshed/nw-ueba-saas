var determineSocketUrl = require('../../../common').determineSocketUrl;

module.exports = function(environment) {

  var socketUrl = determineSocketUrl(environment, '/cms/socket');

  // remove this line when mock server in place
  socketUrl = '/cms/socket';

  return {
    'live-search-resource-types': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/cms/search/get-resource-types',
        requestDestination: '/ws/cms/search/get-resource-types'
      }
    },
    'live-search-mediums': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/cms/search/get-resource-mediums',
        requestDestination: '/ws/cms/search/get-resource-mediums'
      }
    },
    'live-search-meta-keys': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/cms/search/get-resource-meta-keys',
        requestDestination: '/ws/cms/search/get-resource-meta-keys'
      }
    },
    'live-search-meta-values': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/cms/search/get-resource-meta-values',
        requestDestination: '/ws/cms/search/get-resource-meta-values'
      }
    },
    'live-search-categories': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/cms/search/get-resource-categories',
        requestDestination: '/ws/cms/search/get-resource-categories'
      }
    },
    'live-search': {
      socketUrl,
      query: {
        subscriptionDestination: '/cms/search/search',
        requestDestination: '/ws/cms/search/search'
      }
    }
  };
};