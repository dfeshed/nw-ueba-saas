/* eslint-env node */

const common = require('../../../common');
const investigateConfigGen = require('../../../investigate').socketRouteGenerator;
const contextConfigGen = require('../../../context').socketRouteGenerator;
const respondConfigGen = require('../../../respond').socketRouteGenerator;
const responseConfigGen = require('./response');
const adminConfigGen = require('./administration');
const testConfigGen = require('./test');

// order matters, first config in wins if there are matching configs
const configGenerators = [
  testConfigGen,
  investigateConfigGen,
  responseConfigGen, // old / deprecated / will be removed soon
  respondConfigGen, // new official respond
  contextConfigGen,
  adminConfigGen
];

var socketConfig = null;

const generateSocketConfiguration = function(environment) {

  // this gets called a looooot on ember start up so use cache
  if (socketConfig) {
    return socketConfig;
  }

  socketConfig = common.mergeSocketConfigs(configGenerators, environment);

  // UNCOMMENT to see combined socketConfig on startup
  // console.log(socketConfig)

  return socketConfig;
};

module.exports = generateSocketConfiguration;
