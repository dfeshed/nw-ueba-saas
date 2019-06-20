/* eslint-env node */
const common = require('../../../common');
const preferencesConfigGen = require('../../../preferences').socketRouteGenerator;
let mergedConfig;

const hostsConfigGen = function(env) {

  const socketUrl = common.determineSocketUrl(env, '/endpoint/socket');
  const contextSocketUrl = common.determineSocketUrl(env, '/contexthub/socket');

  return {
    'endpoint-server-ping': {
      socketUrl
    },
    'investigate-service': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/endpoint/investigate/servers',
        requestDestination: '/ws/endpoint/investigate/servers',
        endpointPreference: 'any'
      },
      serviceId: {
        subscriptionDestination: '/user/queue/endpoint/investigate/service-id',
        requestDestination: '/ws/endpoint/investigate/service-id'
      }
    },
    'endpoint-server': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/endpoint/server/get-all',
        requestDestination: '/ws/endpoint/server/get-all'
      }
    },
    endpoint: {
      socketUrl,
      export: {
        subscriptionDestination: '/user/queue/endpoint/machine/property/export',
        requestDestination: '/ws/endpoint/machine/property/export'
      },
      machines: {
        subscriptionDestination: '/user/queue/endpoint/machine/search',
        requestDestination: '/ws/endpoint/machine/search'
      },
      hostDownload: {
        subscriptionDestination: '/user/queue/endpoint/download/search',
        requestDestination: '/ws/endpoint/download/search'
      },
      hostDownloadDelete: {
        subscriptionDestination: '/user/queue/endpoint/download/delete',
        requestDestination: '/ws/endpoint/download/delete'
      },
      saveLocalMFTCopy: {
        subscriptionDestination: '/user/queue/endpoint/memory/export',
        requestDestination: '/ws/endpoint/memory/export'
      },
      'machine-schema': {
        subscriptionDestination: '/user/queue/endpoint/machine/schema',
        requestDestination: '/ws/endpoint/machine/schema',
        endpointPreference: 'any'
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
      restrictedList: {
        subscriptionDestination: '/user/queue/endpoint/file/status/restricted',
        requestDestination: '/ws/endpoint/file/status/restricted'
      },
      fileAnalysis: {
        subscriptionDestination: '/user/queue/endpoint/file/analyze',
        requestDestination: '/ws/endpoint/file/analyze'
      },
      fileAnalysisStringFormat: {
        subscriptionDestination: '/user/queue/endpoint/file/get-strings',
        requestDestination: '/ws/endpoint/file/get-strings'
      },
      saveLocalCopy: {
        subscriptionDestination: '/user/queue/endpoint/file/export',
        requestDestination: '/ws/endpoint/file/export'
      },
      fileAnalysisTextFormat: {
        subscriptionDestination: '/user/queue/endpoint/file/get-encoded-data',
        requestDestination: '/ws/endpoint/file/get-encoded-data'
      },
      downloadMFT: {
        subscriptionDestination: '/user/queue/endpoint/command/download-mft',
        requestDestination: '/ws/endpoint/command/download-mft'
      }
    },
    'context-service': {
      socketUrl: contextSocketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/contexthub/context/lookup',
        requestDestination: '/ws/contexthub/context/lookup',
        cancelDestination: '/ws/contexthub/context/cancel'
      },
      setFileStatus: {
        subscriptionDestination: '/user/queue/contexthub/file/status/set',
        requestDestination: '/ws/contexthub/file/status/set'
      },
      getFileStatus: {
        subscriptionDestination: '/user/queue/contexthub/context/data-source/find',
        requestDestination: '/ws/contexthub/context/data-source/find'
      }
    },
    filters: {
      socketUrl,
      saveFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/set',
        requestDestination: '/ws/endpoint/filter/set',
        endpointPreference: 'any'
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

  const configGenerators = [hostsConfigGen, preferencesConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
