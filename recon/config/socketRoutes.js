module.exports = function(environment) {

  // When running jenkins tests, the MOCK_PORT
  // is set to any of a number of possible ports
  // so need to get from 'process.env'
  let socketUrl;
  if (environment === 'development' || environment === 'test') {
    let mockPort = process.env.MOCK_PORT || 9999;
    socketUrl = 'http://localhost:' + mockPort + '/socket/';
  } else {
    socketUrl = '/investigate/socket';
  }

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
        subscriptionDestination: '/user/queue/investigate/alias',
        requestDestination: '/ws/investigate/alias'
      }
    },
    'reconstruction-summary': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/investigate/reconstruct/session-summary',
        requestDestination: '/ws/investigate/reconstruct/session-summary'
      }
    }
  };
}