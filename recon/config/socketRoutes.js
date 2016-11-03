var determineSocketUrl = require('mock-server/util').determineSocketUrl;

module.exports = function(environment) {

  var socketUrl = determineSocketUrl(environment, '/investigate/socket');

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
    'reconstruction-file-data': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-files',
        requestDestination: '/ws/investigate/reconstruct/session-files'
      }
    },
    'reconstruction-file-extract-job-id': {
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
}
