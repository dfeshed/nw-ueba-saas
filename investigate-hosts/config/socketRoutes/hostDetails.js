/* eslint-env node */
const common = require('../../../common');

module.exports = function(env) {

  const socketUrl = common.determineSocketUrl(env, '/endpoint/socket');

  return {
    endpoint: {
      socketUrl,
      getAllSnapShots: {
        subscriptionDestination: '/user/queue/endpoint/data/machine/snapshots',
        requestDestination: '/ws/endpoint/data/machine/snapshots'
      },
      getHostDetails: {
        subscriptionDestination: '/user/queue/endpoint/data/machine/detail',
        requestDestination: '/ws/endpoint/data/machine/detail'
      },
      getFileContextList: {
        subscriptionDestination: '/user/queue/endpoint/data/filecontext/list',
        requestDestination: '/ws/endpoint/data/filecontext/list'
      },
      exportFileContext: {
        subscriptionDestination: '/user/queue/endpoint/data/scandata/export',
        requestDestination: '/ws/endpoint/data/scandata/export'
      },
      getHostFilesPages: {
        subscriptionDestination: '/user/queue/endpoint/data/filecontext/listpage',
        requestDestination: '/ws/endpoint/data/filecontext/listpage'
      },
      fileContextSearch: {
        subscriptionDestination: '/user/queue/endpoint/data/filecontext/search',
        requestDestination: '/ws/endpoint/data/filecontext/search/stream'
      }
    }
  };
};
