/* eslint-env node */

const common = require('../../common');
const { determineSocketUrl } = common;

const springboardConfigGen = function(env) {
  const socketUrl = determineSocketUrl(env, '/springboard/socket');
  return {
    springboard: {
      socketUrl,
      all: {
        subscriptionDestination: '/user/queue/springboard/all',
        requestDestination: '/ws/springboard/all'
      },
      query: {
        subscriptionDestination: '/user/queue/springboard/widget/query',
        requestDestination: '/ws/springboard/widget/query'
      }
    }
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  springboardConfigGen
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
