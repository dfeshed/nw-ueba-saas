/* eslint-env node */
const reconConfigGen = require('../../recon').socketRouteGenerator;
const contextConfigGen = require('../../context').socketRouteGenerator;
const preferencesConfigGen = require('../../preferences').socketRouteGenerator;
const common = require('../../common');
let mergedConfig;

const investigateConfigGen = function(env) {
  const socketUrl = common.determineSocketUrl(env, '/investigate/socket');

  return {
    'core-service': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/investigate/endpoints',
        requestDestination: '/ws/investigate/endpoints'
      }
    },
    'core-event': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events',
        requestDestination: '/ws/investigate/events/stream'
      }
    },
    'core-event-count': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events/count',
        requestDestination: '/ws/investigate/events/count'
      }
    },
    'core-event-log': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/log-data',
        requestDestination: '/ws/investigate/reconstruct/log-data/stream'
      }
    },
    'core-event-timeline': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/timeline',
        requestDestination: '/ws/investigate/timeline'
      }
    },
    'core-meta-key': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/languages',
        requestDestination: '/ws/investigate/languages'
      }
    },
    'core-meta-alias': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/aliases',
        requestDestination: '/ws/investigate/aliases'
      }
    },
    'core-meta-value': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/meta/values',
        requestDestination: '/ws/investigate/meta/values/stream',
        cancelDestination: '/ws/investigate/cancel'
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

  const configGenerators = [investigateConfigGen, reconConfigGen, contextConfigGen, preferencesConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
