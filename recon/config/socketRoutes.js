/* eslint-env node */

const common = require('../../common');
const preferencesConfigGen = require('../../preferences').socketRouteGenerator;

let mergedConfig;
const reconValueConfigGen = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/investigate/socket');

  return {
    'core-event': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events',
        requestDestination: '/ws/investigate/events/stream'
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
    'reconstruction-summary': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-summary',
        requestDestination: '/ws/investigate/reconstruct/session-summary'
      }
    },
    'reconstruction-packet-data': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-packets',
        requestDestination: '/ws/investigate/reconstruct/session-packets/stream'
      }
    },
    'reconstruction-text-data': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-text',
        requestDestination: '/ws/investigate/reconstruct/session-text/stream'
      }
    },
    'reconstruction-file-data': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-files',
        requestDestination: '/ws/investigate/reconstruct/session-files'
      }
    },
    'reconstruction-extract-job-id': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/extract/file',
        requestDestination: '/ws/investigate/extract/file'
      }
    },
    'investigate-notification': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/notification',
        requestDestination: '/ws/investigate/notification'
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
  const configGenerators = [reconValueConfigGen, preferencesConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
