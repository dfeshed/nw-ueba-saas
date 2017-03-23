/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/response/socket');

  return {
    incidents: {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/incidents',
        requestDestination: '/ws/response/incidents'
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
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/incidents/delete',
        requestDestination: '/ws/response/incidents/delete'
      }
    },
    'incidents-count': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/incidents/count',
        requestDestination: '/ws/response/incidents/count'
      }
    },
    storyline: {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/incident/storyline',
        requestDestination: '/ws/response/incident/storyline'
      }
    },
    'category-tags': {
      socketUrl,
      stream: {
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
    },
    events: {
      socketUrl,
      stream: {
        defaultStreamLimit: 1000,
        subscriptionDestination: '/user/queue/alert/events',
        requestDestination: '/ws/response/alert/events'
      }
    },
    users: {
      socketUrl,
      findAll: {
        defaultStreamLimit: 1000,
        subscriptionDestination: '/user/queue/users/all',
        requestDestination: '/ws/response/users/all'
      }
    },
    'priority-types': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/options/priority',
        requestDestination: '/ws/response/options/priority'
      }
    },
    'status-types': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/options/status',
        requestDestination: '/ws/response/options/status'
      }
    }
  };
};
