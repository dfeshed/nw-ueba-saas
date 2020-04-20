/* eslint-env node */
const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/endpoint/socket');

  return {
    'endpoint-server-ping': {
      socketUrl
    },
    endpoint: {
      socketUrl
    }
  };
};
