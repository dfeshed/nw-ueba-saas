/* eslint-env node */

const common = require('../../../common');
const investigateConfigGen = require('../../../investigate-events').socketRouteGenerator;
const investigateConfigFiles = require('../../../investigate-files').socketRouteGenerator;
const contextConfigGen = require('../../../context').socketRouteGenerator;
const preferencesConfigGen = require('../../../preferences').socketRouteGenerator;
const respondConfigGen = require('../../../respond').socketRouteGenerator;
const adminConfigGen = require('./administration');

// order matters, first config in wins if there are matching configs
const configGenerators = [
  investigateConfigGen,
  investigateConfigFiles,
  respondConfigGen,
  contextConfigGen,
  preferencesConfigGen,
  adminConfigGen
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
