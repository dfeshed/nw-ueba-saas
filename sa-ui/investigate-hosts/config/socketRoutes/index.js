/* eslint-env node */
const common = require('../../../common');
const agentSocket = require('./agentRoutes');
const endpointSocket = require('./endpointRoutes');
const hostDetailSocket = require('./hostDetails');
const adminSocket = require('./adminRoutes');
const contextSocket = require('../../../context').socketRouteGenerator;

const configGenerator = [
  agentSocket,
  endpointSocket,
  hostDetailSocket,
  adminSocket,
  contextSocket
];


let config = null;

const generateSocketConfiguration = function(environment) {

  if (config) {
    return config;
  }
  // as of ember 2.14, for some reason environment can be undefined
  if (!environment) {
    return {};
  }

  config = common.mergeSocketConfigs(configGenerator, environment);

  return config;
};

module.exports = generateSocketConfiguration;
