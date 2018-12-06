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
      getAgentStatus: {
        subscriptionDestination: '/user/queue/endpoint/machine/status',
        requestDestination: '/ws/endpoint/machine/status'
      },
      downloadFileToServer: {
        subscriptionDestination: '/user/queue/endpoint/command/download-file',
        requestDestination: '/ws/endpoint/command/download-file'
      }
    }
  };
};
