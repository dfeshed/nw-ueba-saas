/* eslint-disable */

'use strict';

module.exports = function(/* environment, appConfig */) {
  var ENV = {
    // Used for tests run right out of streaming-data addon
    socketRoutes: {
      test: {
        socketUrl: '/test/socket',
        stream: {
          subscriptionDestination: '/user/queue/test/data',
          requestDestination: '/ws/test/data/stream'
        }
      }
    },
    socketDebug: false
  };

  return ENV;
};
