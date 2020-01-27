/* eslint-env node */
const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/metric/socket');

  return {
    'metric-server-ping': {
      socketUrl
    },
    'health-wellness': {
      socketUrl
    }
  };
};
