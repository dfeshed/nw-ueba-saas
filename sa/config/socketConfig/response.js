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
        subscriptionDestination: '/user/queue/incidents/%@',
        requestDestination: '/ws/response/incidents',
        cancelDestination: '/ws/response/cancel'
      },
      storyline:{
        subscriptionDestination: '/user/queue/incident/storyline',
        requestDestination: '/ws/response/incident/storyline'
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
    storyline: {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/incident/storyline',
        requestDestination: '/ws/response/incident/storyline'
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
    },
    'journal-entry': {
      socketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/journal/create',
        requestDestination: '/ws/response/journal/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/journal/update',
        requestDestination: '/ws/response/journal/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/journal/delete',
        requestDestination: '/ws/response/journal/delete'
      }
    }
  };
}
