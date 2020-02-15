/* eslint-env node */

const common = require('../../../common');

// order matters, first config in wins if there are matching configs


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

  socketConfig = common.mergeSocketConfigs([], environment);

  // UNCOMMENT to see combined socketConfig on startup
  // console.log(socketConfig)

  return null;
};

module.exports = generateSocketConfiguration;
