/* eslint-env node */
const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/metrics/socket');

  return {
    'metrics-server-ping': {
      socketUrl
    },
    'health-wellness': {
      socketUrl,
      getMonitors: {
        subscriptionDestination: '/user/queue/metrics/monitor/get-all',
        requestDestination: '/ws/metrics/monitor/get-all'
      },
    }
  };
};
