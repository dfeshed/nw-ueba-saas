module.exports = function(environment) {

  if (!(environment === 'development' || environment === 'test')) {
    return {};
  }

  // Used for automated Ember tests. Remove this and tests will fail.
  return {
    test: {
      socketUrl: '/test/socket',
      stream: {
        subscriptionDestination: '/user/queue/test/data',
        requestDestination: '/ws/test/data/stream'
      },
      query: {
        subscriptionDestination: '/user/queue/test/data',
        requestDestination: '/ws/test/data/query'
      },
      findRecord: {
        subscriptionDestination: '/user/queue/test/data',
        requestDestination: '/ws/test/data/find'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/test/data',
        requestDestination: '/ws/test/data/update'
      }
    }
  }
};