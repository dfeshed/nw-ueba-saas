/* eslint-env node */
// THE FOLLOWING IS AN EXAMPLE, WILL NEED TO BE CHANGED PER ENGINE

const common = require('../../common');
const preferencesConfigGen = require('../../preferences').socketRouteGenerator;
let mergedConfig;

const filesConfigGen = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    'endpoint-server-ping': {
      socketUrl
    },
    'investigate-service': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/endpoint/investigate/servers',
        requestDestination: '/ws/endpoint/investigate/servers'
      }
    },
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
        subscriptionDestination: '/user/queue/endpoint/file/property/export',
        requestDestination: '/ws/endpoint/file/property/export'
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
    },
    filesPreferences: {
      socketUrl,
      getPreferences: {
        subscriptionDestination: '/user/queue/endpoint/preferences/get',
        requestDestination: '/ws/endpoint/preferences/get'
      },
      setPreferences: {
        subscriptionDestination: '/user/queue/endpoint/preferences/set',
        requestDestination: '/ws/endpoint/preferences/set'
      }
    }
  };
};

module.exports = function(environment) {
  // cache it, prevents super spammy console as this gets called
  // many times during startup
  if (mergedConfig) {
    return mergedConfig;
  }

  // as of ember 2.14, for some reason environment can be undefined
  if (!environment) {
    return {};
  }

  const configGenerators = [filesConfigGen, preferencesConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
