/* eslint-env node */
'use strict';

// When running jenkins tests, the MOCK_PORT
// is set to any of a number of possible ports
// so need to get from 'process.env'
const mockPort = process.env.MOCK_PORT || 9999;
const socketUrl = `http://localhost:${mockPort}/socket/`;

module.exports = function(/* environment, appConfig */) {
  const ENV = {
    // Used for tests run right out of streaming-data addon
    socketRoutes: {
      test: {
        socketUrl,
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
        },
        'promise/_5': {
          subscriptionDestination: '/test/subscription/promise/_5',
          requestDestination: '/test/request/promise/_5'
        },
        'promise/_6': {
          subscriptionDestination: '/test/subscription/promise/_6',
          requestDestination: '/test/request/promise/_6'
        },
        'stream/_1': {
          subscriptionDestination: '/test/subscription/stream/_1',
          requestDestination: '/test/request/stream/_1'
        },
        'stream/_2': {
          subscriptionDestination: '/test/subscription/stream/_2',
          requestDestination: '/test/request/stream/_2'
        },
        'stream/_3': {
          subscriptionDestination: '/test/subscription/stream/_3',
          requestDestination: '/test/request/stream/_3'
        },
        'stream/_4': {
          subscriptionDestination: '/test/subscription/stream/_4',
          requestDestination: '/test/request/stream/_4'
        },
        'stream/_5': {
          subscriptionDestination: '/test/subscription/stream/_5',
          requestDestination: '/test/request/stream/_5'
        },
        'stream/_6': {
          subscriptionDestination: '/test/subscription/stream/_6',
          requestDestination: '/test/request/stream/_6'
        }
      }
    }
  };
  return ENV;
};
