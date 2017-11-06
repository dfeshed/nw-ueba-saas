/* eslint-env node */
const common = require('../../../common');

module.exports = function(env) {

  const socketUrl = common.determineSocketUrl(env, '/endpoint/socket');

  return {
    endpoint: {
      socketUrl,
      getAllSnapShots: {
        subscriptionDestination: '/user/queue/endpoint/machine/snapshots',
        requestDestination: '/ws/endpoint/machine/snapshots'
      },
      getHostDetails: {
        subscriptionDestination: '/user/queue/endpoint/machine/detail',
        requestDestination: '/ws/endpoint/machine/detail'
      },
      getFileContextList: {
        subscriptionDestination: '/user/queue/endpoint/filecontext/list',
        requestDestination: '/ws/endpoint/filecontext/list'
      },
      exportFileContext: {
        subscriptionDestination: '/user/queue/endpoint/machine/proporty/export',
        requestDestination: '/ws/endpoint/machine/proporty/export'
      },
      getHostFilesPages: {
        subscriptionDestination: '/user/queue/endpoint/filecontext/list-page',
        requestDestination: '/ws/endpoint/filecontext/list-page'
      },
      fileContextSearch: {
        subscriptionDestination: '/user/queue/endpoint/filecontext/search',
        requestDestination: '/ws/endpoint/filecontext/search/stream'
      }
    }
  };
};
