/* eslint-env node */
const common = require('../../../common');

module.exports = function(env) {

  const socketUrl = common.determineSocketUrl(env, '/endpoint/socket');

  return {
    endpoint: {
      socketUrl,
      export: {
        subscriptionDestination: '/user/queue/endpoint/machine/export',
        requestDestination: '/ws/endpoint/machine/export'
      },
      machines: {
        subscriptionDestination: '/user/queue/endpoint/machine/search',
        requestDestination: '/ws/endpoint/machine/search'
      },
      'machine-schema': {
        subscriptionDestination: '/user/queue/endpoint/machine/schema',
        requestDestination: '/ws/endpoint/machine/schema'
      },
      getAllFilters: {
        subscriptionDestination: '/user/queue/endpoint/filter/get-all',
        requestDestination: '/ws/endpoint/filter/get-all'
      },
      agentCount: {
        subscriptionDestination: '/user/queue/endpoint/machine/aggregate/group-count',
        requestDestination: '/ws/endpoint/machine/aggregate/group-count'
      },
      getProcessList: {
        subscriptionDestination: '/user/queue/endpoint/process/list',
        requestDestination: '/ws/endpoint/process/list'
      },
      getProcessTree: {
        subscriptionDestination: '/user/queue/endpoint/process/tree',
        requestDestination: '/ws/endpoint/process/tree'
      },
      getProcess: {
        subscriptionDestination: '/user/queue/endpoint/process/get',
        requestDestination: '/ws/endpoint/process/get'
      },
      getHostFileContext: {
        subscriptionDestination: '/user/queue/endpoint/process/get-all',
        requestDestination: '/ws/endpoint/process/get-all'
      },
      saveFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/set',
        requestDestination: '/ws/endpoint/filter/set'
      }
    },
    search: {
      socketUrl,
      removeSearch: {
        subscriptionDestination: '/user/queue/endpoint/data/filter/remove',
        requestDestination: '/ws/endpoint/data/filter/remove'
      }
    }
  };
};
