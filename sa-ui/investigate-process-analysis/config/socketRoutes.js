/* eslint-env node */

const common = require('../../common');

const cancelDestination = '/ws/investigate/cancel';

const processAnalysisConfigGen = function(env) {
  const endpointSocketUrl = common.determineSocketUrl(env, '/endpoint/socket');
  const eventsSocketURL = common.determineSocketUrl(env, '/investigate/socket');
  const respondSocketURL = common.determineSocketUrl(env, '/respond/socket');
  return {
    'endpoint': {
      socketUrl: endpointSocketUrl,
      getProcessAnalysisDetails: {
        subscriptionDestination: '/user/queue/endpoint/file/get',
        requestDestination: '/ws/endpoint/file/get'
      }
    },
    'core-event-count-distinct': {
      socketUrl: eventsSocketURL,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events/countdistinct',
        requestDestination: '/ws/investigate/events/countdistinct'
      }
    },
    'core-event': {
      socketUrl: eventsSocketURL,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events',
        requestDestination: '/ws/investigate/events/stream',
        cancelDestination
      }
    },
    'core-service': {
      socketUrl: eventsSocketURL,
      findAll: {
        subscriptionDestination: '/user/queue/investigate/endpoints',
        requestDestination: '/ws/investigate/endpoints'
      }
    },
    'core-summary': {
      socketUrl: eventsSocketURL,
      query: {
        subscriptionDestination: '/user/queue/investigate/summary',
        requestDestination: '/ws/investigate/summary'
      }
    },
    'risk-score': {
      socketUrl: respondSocketURL,
      localRiskScore: {
        subscriptionDestination: '/user/queue/risk/local/score',
        requestDestination: '/ws/respond/risk/local/score'
      }
    },
    'host-count' : {
      socketUrl: endpointSocketUrl,
      getHostCount: {
        subscriptionDestination: '/user/queue/endpoint/file/risky-hosts',
        requestDestination: '/ws/endpoint/file/risky-hosts'
      }
    },
    'respond-server-ping': {
      socketUrl: respondSocketURL
    },
    'respond-server': {
      socketUrl: respondSocketURL,
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
    'core-meta-value': {
      socketUrl: eventsSocketURL,
      stream: {
        subscriptionDestination: '/user/queue/investigate/meta/values',
        requestDestination: '/ws/investigate/meta/values/stream',
        cancelDestination: '/ws/investigate/cancel'
      }
    }
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  processAnalysisConfigGen
];

let socketConfig = null;

const generateSocketConfiguration = function(environment) {

  // this gets called a looooot on ember start up so use cache
  if (socketConfig) {
    return socketConfig;
  }

  // as of ember 2.14, for some reason environment can be undefined
  if (!environment) {
    return {};
  }

  socketConfig = common.mergeSocketConfigs(configGenerators, environment);

  // UNCOMMENT to see combined socketConfig on startup
  // console.log(socketConfig)

  return socketConfig;
};

module.exports = generateSocketConfiguration;
