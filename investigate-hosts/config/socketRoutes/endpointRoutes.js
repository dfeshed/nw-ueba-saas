/* eslint-env node */
const common = require('../../../common');

module.exports = function(env) {

  const socketUrl = common.determineSocketUrl(env, '/endpoint/socket');

  return {
    endpoint: {
      socketUrl,
      export: {
        subscriptionDestination: '/user/queue/endpoint/data/machine/export',
        requestDestination: '/ws/endpoint/data/machine/export'
      },
      machines: {
        subscriptionDestination: '/user/queue/endpoint/data/machine/search',
        requestDestination: '/ws/endpoint/data/machine/search'
      },
      'machine-schema': {
        subscriptionDestination: '/user/queue/endpoint/data/machine/schema',
        requestDestination: '/ws/endpoint/data/machine/schema'
      },
      getAllFilters: {
        subscriptionDestination: '/user/queue/endpoint/data/filter/getall',
        requestDestination: '/ws/endpoint/data/filter/getall'
      },
      agentCount: {
        subscriptionDestination: '/user/queue/endpoint/data/machine/aggregate/count/group',
        requestDestination: '/ws/endpoint/data/machine/aggregate/count/group'
      },
      getProcessList: {
        subscriptionDestination: '/user/queue/endpoint/data/process/list',
        requestDestination: '/ws/endpoint/data/process/list'
      },
      getProcessTree: {
        subscriptionDestination: '/user/queue/endpoint/data/process/tree',
        requestDestination: '/ws/endpoint/data/process/tree'
      },
      getProcess: {
        subscriptionDestination: '/user/queue/endpoint/data/process/get',
        requestDestination: '/ws/endpoint/data/process/get'
      },
      getHostFileContext: {
        subscriptionDestination: '/user/queue/endpoint/data/process/getAll',
        requestDestination: '/ws/endpoint/data/process/getAll'
      },
      saveFilter: {
        subscriptionDestination: '/user/queue/endpoint/data/filter/set',
        requestDestination: '/ws/endpoint/data/filter/set'
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
