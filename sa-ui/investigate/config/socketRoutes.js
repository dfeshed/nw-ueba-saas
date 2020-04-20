/* eslint-env node */

const common = require('../../common');
const investigateEventsConfig = require('../../investigate-events').socketRouteGenerator;
const investigateFilesConfig = require('../../investigate-files').socketRouteGenerator;
const investigateHostsConfig = require('../../investigate-hosts').socketRouteGenerator;
const investigateProcessAnalysisConfig = require('../../investigate-process-analysis').socketRouteGenerator;

// order matters, first config in wins if there are matching configs
const configGenerators = [
  investigateProcessAnalysisConfig,
  investigateEventsConfig,
  investigateFilesConfig,
  investigateHostsConfig
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
