/* eslint-env node */
// THE FOLLOWING IS AN EXAMPLE, WILL NEED TO BE CHANGED PER ENGINE

const common = require('../../common');
const preferencesConfigGen = require('../../preferences').socketRouteGenerator;
const licenseConfigGen = require('../../license').socketRouteGenerator;
const contextConfigGen = require('../../context').socketRouteGenerator;
let mergedConfig;

const filesConfigGen = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');
  const contextSocketUrl = common.determineSocketUrl(environment, '/contexthub/socket');
  const investigateSocketUrl = common.determineSocketUrl(environment, '/investigate/socket');
  const riskScoreSocketUrl = common.determineSocketUrl(environment, '/risk/score/socket');
  const respondSocketUrl = common.determineSocketUrl(environment, '/respond/socket');


  return {
    'endpoint-server-ping': {
      socketUrl
    },
    'contexthub-server-ping': {
      socketUrl: contextSocketUrl
    },
    'risk-scoring-server-ping': {
      socketUrl: riskScoreSocketUrl
    },
    'respond-server': {
      socketUrl: respondSocketUrl,
      'alert-events': {
        subscriptionDestination: '/user/queue/alerts/events',
        requestDestination: '/ws/respond/alerts/events'
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
        requestDestination: '/ws/investigate/events/stream'
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
      }
    },
    'endpoint-server': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/endpoint/server/get-all',
        requestDestination: '/ws/endpoint/server/get-all'
      }
    },
    'risk-score-server': {
      socketUrl: riskScoreSocketUrl,
      getFileContext: {
        subscriptionDestination: '/user/queue/risk/score/file/context/get',
        requestDestination: '/ws/risk/score/file/context/get'
      },
      resetRiskScore: {
        subscriptionDestination: '/user/queue/risk/score/file/context/reset',
        requestDestination: '/ws/risk/score/file/context/reset'
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
