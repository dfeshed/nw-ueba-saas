/* eslint-disable */

// When running jenkins tests, the MOCK_PORT
// is set to any of a number of possible ports
// so need to get from 'process.env'
var mockPort = process.env.MOCK_PORT || 9999;
var socketUrl = 'http://localhost:' + mockPort + '/socket/';

module.exports = function(environment, appConfig) {
  var ENV = {
    // Used for tests run right out of streaming-data addon
    socketRoutes: {
      test: {
        socketUrl: socketUrl,
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
