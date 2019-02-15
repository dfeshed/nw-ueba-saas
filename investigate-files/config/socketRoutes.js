/* eslint-env node */
// THE FOLLOWING IS AN EXAMPLE, WILL NEED TO BE CHANGED PER ENGINE

const common = require('../../common');
const preferencesConfigGen = require('../../preferences').socketRouteGenerator;
const licenseConfigGen = require('../../license').socketRouteGenerator;
const contextConfigGen = require('../../context').socketRouteGenerator;

const cancelDestination = '/ws/investigate/cancel';

let mergedConfig;

const filesConfigGen = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');
  const contextSocketUrl = common.determineSocketUrl(environment, '/contexthub/socket');
  const investigateSocketUrl = common.determineSocketUrl(environment, '/investigate/socket');
  const respondSocketUrl = common.determineSocketUrl(environment, '/respond/socket');


  return {
    'endpoint-server-ping': {
      socketUrl
    },
    'contexthub-server-ping': {
      socketUrl: contextSocketUrl
    },
    'respond-server-ping': {
      socketUrl: respondSocketUrl
    },
    'respond-server': {
      socketUrl: respondSocketUrl,
      'get-events': {
        subscriptionDestination: '/user/queue/alerts/events/batch',
        requestDestination: '/ws/respond/alerts/events/batch'
      },
      getFileContext: {
        subscriptionDestination: '/user/queue/risk/context/file',
        requestDestination: '/ws/respond/risk/context/file'
      },
      getDetailFileContext: {
        subscriptionDestination: '/user/queue/risk/context/detail/file',
        requestDestination: '/ws/respond/risk/context/detail/file'
      },
      resetRiskScore: {
        subscriptionDestination: '/user/queue/risk/context/file/reset',
        requestDestination: '/ws/respond/risk/context/file/reset'
      }
    },
    'investigate-service': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/endpoint/investigate/servers',
        requestDestination: '/ws/endpoint/investigate/servers'
      },
      serviceId: {
        subscriptionDestination: '/user/queue/endpoint/investigate/service-id',
        requestDestination: '/ws/endpoint/investigate/service-id'
      }
    },
    'core-event-count-distinct': {
      socketUrl: investigateSocketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events/countdistinct',
        requestDestination: '/ws/investigate/events/countdistinct'
      }
    },
    'core-meta-value': {
      socketUrl: investigateSocketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/meta/values',
        requestDestination: '/ws/investigate/meta/values/stream',
        cancelDestination: '/ws/investigate/cancel'
      }
    },
    'core-event': {
      socketUrl: investigateSocketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events',
        requestDestination: '/ws/investigate/events/stream',
        cancelDestination
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
      getFiles: {
        subscriptionDestination: '/user/queue/endpoint/file/get',
        requestDestination: '/ws/endpoint/file/get'
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
      },
      getRemediation: {
        subscriptionDestination: '/user/queue/endpoint/certificate/remediation/allowed',
        requestDestination: '/ws/endpoint/certificate/remediation/allowed'
      },
      downloadFileToServer: {
        subscriptionDestination: '/user/queue/endpoint/file/request-file',
        requestDestination: '/ws/endpoint/file/request-file'
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
    endpoint: {
      socketUrl,
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
      }
    },
    'endpoint-server': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/endpoint/server/get-all',
        requestDestination: '/ws/endpoint/server/get-all'
      }
    },
    'endpoint-certificates': {
      socketUrl,
      getCertificates: {
        subscriptionDestination: '/user/queue/endpoint/certificate/search',
        requestDestination: '/ws/endpoint/certificate/search'
      }
    },
    'context-data': {
      socketUrl: contextSocketUrl,
      setCertificateStatus: {
        subscriptionDestination: '/user/queue/contexthub/certificate/status/set',
        requestDestination: '/ws/contexthub/certificate/status/set'
      }
    },
    filters: {
      socketUrl,
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

  const configGenerators = [contextConfigGen, filesConfigGen, preferencesConfigGen, licenseConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
