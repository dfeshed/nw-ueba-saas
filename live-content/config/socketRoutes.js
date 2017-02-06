/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/cms/socket');

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
      findAll: {
        subscriptionDestination: '/cms/search/search',
        requestDestination: '/ws/cms/search/search'
      }
    }
  };
};
