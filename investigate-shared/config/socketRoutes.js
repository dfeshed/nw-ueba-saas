/* eslint-env node */

const common = require('../../common');

let mergedConfig;

const investigateConfigGen = function(env) {
  const eventsSocketURL = common.determineSocketUrl(env, '/investigate/socket');
  return {
    'core-event': {
      eventsSocketURL,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events',
        requestDestination: '/ws/investigate/events/stream'
      }
    }
  };
};

module.exports = function(environment) {
  // cache it, prevents super spammy console as this gets called
  // many times during startup
  if (mergedConfig) {
    return mergedConfig;
  }
  // as of ember 2.14, for some reason environment can be undefined
  if (!environment) {
    return {};
  }
  const configGenerators = [investigateConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
