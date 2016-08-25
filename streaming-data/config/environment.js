/* eslint-disable */

module.exports = function(environment, appConfig) {
  var ENV = {
    // Used for tests run right out of streaming-data addon
    socketRoutes: {
      test: {
        socketUrl: 'http://localhost:9999/socket/',
        'promise/_1': {
          subscriptionDestination: '/test/subscription/promise/_1',
          requestDestination: '/test/request/promise/_1'
        },
        'promise/_2': {
          subscriptionDestination: '/test/subscription/promise/_2',
          requestDestination: '/test/request/promise/_2'
        },
        'promise/_3': {
          subscriptionDestination: '/test/subscription/promise/_3',
          requestDestination: '/test/request/promise/_3'
        },
        'promise/_4': {
          subscriptionDestination: '/test/subscription/promise/_4',
          requestDestination: '/test/request/promise/_4'
        }
      }
    }
  };
  return ENV;
};
