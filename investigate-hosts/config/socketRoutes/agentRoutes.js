/* eslint-env node */

const common = require('../../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    agent: {
      socketUrl,
      commandScan: {
        subscriptionDestination: '/user/queue/endpoint/command/start-scan',
        requestDestination: '/ws/endpoint/command/start-scan'
      },
      stopScan: {
        subscriptionDestination: '/user/queue/endpoint/command/stop-scan',
        requestDestination: '/ws/endpoint/command/stop-scan'
      },
      deleteHosts: {
        subscriptionDestination: '/user/queue/endpoint/machine/remove',
        requestDestination: '/ws/endpoint/machine/remove'
      },
      notify: {
        subscriptionDestination: '/topic/agentstatus/notifications',
        requestDestination: '/dummy/agentstatus/notifications',
        cancelDestination: '/ws/endpoint/agent/cancel'
      }
    }
  };
};
