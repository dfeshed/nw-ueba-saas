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
      downloadProcessDump: {
        subscriptionDestination: '/user/queue/endpoint/command/request-process-dump',
        requestDestination: '/ws/endpoint/command/request-process-dump'
      },
      downloadSystemDump: {
        subscriptionDestination: '/user/queue/endpoint/command/request-system-dump',
        requestDestination: '/ws/endpoint/command/request-system-dump'
      },
      downloadFileToServer: {
        subscriptionDestination: '/user/queue/endpoint/command/download-file',
        requestDestination: '/ws/endpoint/command/download-file'
      },
      downloadWildcardMatchedFilesToServer: {
        subscriptionDestination: '/user/queue/endpoint/command/download-files',
        requestDestination: '/ws/endpoint/command/download-files'
      },
      resetHostRiskScore: {
        subscriptionDestination: '/user/queue/endpoint/machine/remove',
        requestDestination: '/ws/endpoint/machine/remove'
      },
      isolateHost: {
        subscriptionDestination: '/user/queue/endpoint/command/start-isolation',
        requestDestination: '/ws/endpoint/command/start-isolation'
      },
      stopIsolation: {
        subscriptionDestination: '/user/queue/endpoint/command/stop-isolation',
        requestDestination: '/ws/endpoint/command/stop-isolation'
      },
      editExclusionList: {
        subscriptionDestination: '/user/queue/endpoint/command/update-isolation-exclusionList',
        requestDestination: '/ws/endpoint/command/update-isolation-exclusionList'
      }
    }
  };
};
