/* eslint-env node */

const common = require('../../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/contexthub/socket');
  const respondSocketUrl = common.determineSocketUrl(environment, '/respond/socket');

  return {
    'respond-server-ping': {
      socketUrl: respondSocketUrl
    },
    'context-service': {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/contexthub/context/lookup',
        requestDestination: '/ws/contexthub/context/lookup',
        cancelDestination: '/ws/contexthub/context/cancel'
      }
    },
    'respond-server': {
      socketUrl: respondSocketUrl,
      'get-events': {
        subscriptionDestination: '/user/queue/alerts/events/batch',
        requestDestination: '/ws/respond/alerts/events/batch'
      },
      getHostContext: {
        subscriptionDestination: '/user/queue/risk/context/host',
        requestDestination: '/ws/respond/risk/context/host'
      },
      getHostFileContext: {
        subscriptionDestination: '/user/queue/risk/context/file/host',
        requestDestination: '/ws/respond/risk/context/file/host'
      },
      getDetailHostContext: {
        subscriptionDestination: '/user/queue/risk/context/detail/host',
        requestDestination: '/ws/respond/risk/context/detail/host'
      },
      resetHostRiskScore: {
        subscriptionDestination: '/user/queue/risk/context/host/reset',
        requestDestination: '/ws/respond/risk/context/host/reset'
      },
      getFileContext: {
        subscriptionDestination: '/user/queue/risk/context/file',
        requestDestination: '/ws/respond/risk/context/file'
      }
    }
  };
};