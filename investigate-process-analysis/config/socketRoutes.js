/* eslint-env node */

const common = require('../../common');
const processAnalysisConfigGen = function(env) {
  const endpointSocketUrl = common.determineSocketUrl(env, '/endpoint/socket');
  const eventsSocketURL = common.determineSocketUrl(env, '/investigate/socket');
  return {
   'endpoint': {
      socketUrl: endpointSocketUrl,
      getProcessAnalysisDetails: {
        subscriptionDestination: '/user/queue/endpoint/file/get',
        requestDestination: '/ws/endpoint/file/get'
      }
    },
    'core-event': {
      socketUrl: eventsSocketURL,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events',
        requestDestination: '/ws/investigate/events/stream'
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
