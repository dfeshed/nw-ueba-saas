const common = require('../../../common');

module.exports = function (env) {

  const socketUrl = common.determineSocketUrl(env,'/risk/score/socket');

  return {
    'risk-score-server': {
      socketUrl,
      getHostContext: {
        subscriptionDestination: '/user/queue/risk/score/host/context/get',
        requestDestination: '/ws/risk/score/host/context/get'
      }
    },
 }
};