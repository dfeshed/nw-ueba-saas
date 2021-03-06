/* eslint-env node */

const common = require('../../../common');
const investigateConfig = require('../../../investigate').socketRouteGenerator;
const contextConfigGen = require('../../../context').socketRouteGenerator;
const preferencesConfigGen = require('../../../preferences').socketRouteGenerator;
const respondConfigGen = require('../../../respond').socketRouteGenerator;
const configureConfigGen = require('../../../configure').socketRouteGenerator;
const packagerConfigGen = require('../../../packager').socketRouteGenerator;
const endpointRARInstaller = require('../../../endpoint-rar').socketRouteGenerator;
const healthWellnessConfigGen = require('../../../health-wellness').socketRouteGenerator;
const adminEngineConfigGen = require('../../../admin').socketRouteGenerator;
const licenseConfigGen = require('../../../license').socketRouteGenerator;
const springboardConfigGen = require('../../../springboard').socketRouteGenerator;
const adminConfigGen = require('./administration');
const featuresConfigGen = require('./features');

// order matters, first config in wins if there are matching configs
const configGenerators = [
  investigateConfig,
  respondConfigGen,
  configureConfigGen,
  contextConfigGen,
  preferencesConfigGen,
  packagerConfigGen,
  endpointRARInstaller,
  healthWellnessConfigGen,
  adminEngineConfigGen,
  licenseConfigGen,
  springboardConfigGen,
  adminConfigGen,
  featuresConfigGen
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
