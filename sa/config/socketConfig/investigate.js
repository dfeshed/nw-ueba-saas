var determineSocketUrl = require('mock-server/util').determineSocketUrl;

module.exports = function(environment) {

  var socketUrl = determineSocketUrl(environment, '/investigate/socket');

  // remove this line when mock server in place
  socketUrl = '/investigate/socket';

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
        subscriptionDestination: '/user/queue/investigate/alias',
        requestDestination: '/ws/investigate/alias'
      }
    }
  };
}