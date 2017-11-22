/* eslint-env node */
const common = require('../../common');
let mergedConfig;

const scheduleConfig = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    schedule: {
      socketUrl,
      get: {
        subscriptionDestination: '/user/queue/endpoint/policy/get',
        requestDestination: '/ws/endpoint/policy/get'
      },
      update: {
        subscriptionDestination: '/user/queue/endpoint/policy/set',
        requestDestination: '/ws/endpoint/policy/set'
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

  const configGenerators = [scheduleConfig];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
