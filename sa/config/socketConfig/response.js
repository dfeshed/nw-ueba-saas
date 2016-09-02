var determineSocketUrl = require('mock-server/util').determineSocketUrl;

module.exports = function(environment) {

  var socketUrl = determineSocketUrl(environment, '/response/socket');

  // remove this line when mock server in place
  socketUrl = '/response/socket';

  return {
    incident: {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/topic/incidents/%@',
        requestDestination: '/ws/response/incidents',
        cancelDestination: '/ws/response/cancel'
      },
      notify: {
        subscriptionDestination: '/topic/incidents/owner/%@',
        requestDestination: '/dummy/incidents/owner',
        cancelDestination: '/ws/response/cancel'
      },
      queryRecord: {
        subscriptionDestination: '/user/queue/incident/details',
        requestDestination: '/ws/response/incident/details'
      },
      updateRecord: {
        subscriptionDestination: '/queue/incidents/update',
        requestDestination: '/ws/response/incidents/update'
      }
    },
    'category-tags': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/categories',
        requestDestination: '/ws/response/categories'
      }
    },
    alerts: {
      socketUrl,
      stream: {
        defaultStreamLimit: 1000,
        subscriptionDestination: '/user/queue/alerts',
        requestDestination: '/ws/response/alerts'
      }
    }
  };
}
