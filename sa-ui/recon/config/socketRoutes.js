/* eslint-env node */

const common = require('../../common');
const preferencesConfigGen = require('../../preferences').socketRouteGenerator;
const contextGen = require('../../context').socketRouteGenerator;
let mergedConfig;

const reconValueConfigGen = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/investigate/socket');

  return {
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
    'reconstruction-meta': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-meta',
        requestDestination: '/ws/investigate/reconstruct/session-meta'
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
    'reconstruction-email-data': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-emails',
        requestDestination: '/ws/investigate/reconstruct/session-emails/stream'
      }
    },
    'reconstruction-file-data': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-files',
        requestDestination: '/ws/investigate/reconstruct/session-files'
      }
    },

    // endpoint for downloading FILES contained in NETWORK events
    'reconstruction-extract-FILES-job-id': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/extract/file',
        requestDestination: '/ws/investigate/extract/file'
      }
    },

    // endpoint for NETWORK event download
    'reconstruction-extract-NETWORK-job-id': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/extract/pcap',
        requestDestination: '/ws/investigate/extract/pcap'
      }
    },

    // endpoint for LOG event download
    'reconstruction-extract-LOG-job-id': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/extract/log',
        requestDestination: '/ws/investigate/extract/log'
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
  const configGenerators = [reconValueConfigGen, preferencesConfigGen, contextGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
