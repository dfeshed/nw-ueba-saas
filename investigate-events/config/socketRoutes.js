/* eslint-env node */
const reconConfigGen = require('../../recon').socketRouteGenerator;
const contextConfigGen = require('../../context').socketRouteGenerator;
const preferencesConfigGen = require('../../preferences').socketRouteGenerator;
const licenseConfigGen = require('../../license').socketRouteGenerator;
const respondSharedConfigGen = require('../../respond-shared').socketRouteGenerator;

const common = require('../../common');

const cancelDestination = '/ws/investigate/cancel';

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
        requestDestination: '/ws/investigate/events/stream',
        cancelDestination
      }
    },
    'core-event-count': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/events/count',
        requestDestination: '/ws/investigate/events/count',
        cancelDestination
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
    'core-meta-alias': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/aliases',
        requestDestination: '/ws/investigate/aliases'
      }
    },
    'core-summary': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/summary',
        requestDestination: '/ws/investigate/summary'
      }
    },
    'core-meta-value': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/investigate/meta/values',
        requestDestination: '/ws/investigate/meta/values/stream',
        cancelDestination
      }
    },
    'core-queries-validate': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/validate/queries',
        requestDestination: '/ws/investigate/validate/queries'
      }
    },
    'event-settings': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/eventanalysis/settings',
        requestDestination: '/ws/investigate/eventanalysis/settings'
      }
    },
    'recent-queries': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/predicate/get-recent-by-filter',
        requestDestination: '/ws/investigate/predicate/get-recent-by-filter'
      }
    },
    'value-suggestions': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/meta/values/suggestions',
        requestDestination: '/ws/investigate/meta/values/suggestions'
      }
    },
    'meta-key-cache': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/investigate/meta/keys/get-all',
        requestDestination: '/ws/investigate/meta/keys/get-all'
      }
    },
    'columnGroup': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/investigate/column/groups/get',
        requestDestination: '/ws/investigate/column/groups/get'
      },
      post: {
        subscriptionDestination: '/user/queue/investigate/column/groups/set',
        requestDestination: '/ws/investigate/column/groups/set'
      },
      delete: {
        subscriptionDestination: '/user/queue/investigate/column/groups/delete-by-id',
        requestDestination: '/ws/investigate/column/groups/delete-by-id'
      }
    },
    'profileRequest': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/investigate/profile/get-all',
        requestDestination: '/ws/investigate/profile/get-all'
      },
      post: {
        subscriptionDestination: '/user/queue/investigate/profile/set',
        requestDestination: '/ws/investigate/profile/set'
      },
      delete: {
        subscriptionDestination: '/user/queue/investigate/profile/remove',
        requestDestination: '/ws/investigate/profile/remove'
      }
    },
    'metaGroup': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/investigate/meta/groups/get-all',
        requestDestination: '/ws/investigate/meta/groups/get-all'
      }
    },
    'query-hashes': {
      socketUrl,
      find: {
        subscriptionDestination: '/user/queue/investigate/predicate/get-by-id',
        requestDestination: '/ws/investigate/predicate/get-by-id'
      },
      persist: {
        subscriptionDestination: '/user/queue/investigate/predicate/get-by-query',
        requestDestination: '/ws/investigate/predicate/get-by-query'
      }
    },

    // endpoint for NETWORK event download
    'extract-NETWORK-job-id': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/extract/pcap',
        requestDestination: '/ws/investigate/extract/pcap'
      }
    },

    // endpoint for LOG event download
    'extract-LOG-job-id': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/extract/log',
        requestDestination: '/ws/investigate/extract/log'
      }
    },
    'extract-META-job-id': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/extract/meta',
        requestDestination: '/ws/investigate/extract/meta'
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

  const configGenerators = [investigateConfigGen, reconConfigGen, contextConfigGen, preferencesConfigGen, licenseConfigGen, respondSharedConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};
