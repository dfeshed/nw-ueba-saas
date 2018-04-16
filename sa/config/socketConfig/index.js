/* eslint-env node */

const common = require('../../../common');
const investigateConfigGen = require('../../../investigate-events').socketRouteGenerator;
const investigateConfigFiles = require('../../../investigate-files').socketRouteGenerator;
const investigateConfigHosts = require('../../../investigate-hosts').socketRouteGenerator;
const contextConfigGen = require('../../../context').socketRouteGenerator;
const preferencesConfigGen = require('../../../preferences').socketRouteGenerator;
const respondConfigGen = require('../../../respond').socketRouteGenerator;
const configureConfigGen = require('../../../configure').socketRouteGenerator;
const packagerConfigGen = require('../../../packager').socketRouteGenerator;
const investigateSharedConfig = require('../../../investigate-shared').socketRouteGenerator;
const adminConfigGen = require('./administration');

// order matters, first config in wins if there are matching configs
const configGenerators = [
  investigateConfigGen,
  investigateConfigHosts,
  investigateConfigFiles,
  respondConfigGen,
  configureConfigGen,
  contextConfigGen,
  preferencesConfigGen,
  packagerConfigGen,
  investigateSharedConfig,
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
