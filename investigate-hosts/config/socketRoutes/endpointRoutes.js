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
        subscriptionDestination: '/user/queue/endpoint/machine/process/list',
        requestDestination: '/ws/endpoint/machine/process/list'
      },
      getProcessTree: {
        subscriptionDestination: '/user/queue/endpoint/machine/process/tree',
        requestDestination: '/ws/endpoint/machine/process/tree'
      },
      getProcess: {
        subscriptionDestination: '/user/queue/endpoint/machine/process/get',
        requestDestination: '/ws/endpoint/machine/process/get'
      },
      getHostFileContext: {
        subscriptionDestination: '/user/queue/endpoint/machine/process/get-all',
        requestDestination: '/ws/endpoint/machine/process/get-all'
      },
      saveFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/set',
        requestDestination: '/ws/endpoint/filter/set'
      }
    },
    search: {
      socketUrl,
      removeSearch: {
        subscriptionDestination: '/user/queue/endpoint/filter/remove',
        requestDestination: '/ws/endpoint/filter/remove'
      }
    }
  };
};
