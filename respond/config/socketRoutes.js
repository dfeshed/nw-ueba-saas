/* eslint-env node */

const common = require('../../common');
const contextConfigGen = require('../../context').socketRouteGenerator;
const respondConfigGen = function(environment) {

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
      findAll: {
        subscriptionDestination: '/user/queue/categories',
        requestDestination: '/ws/response/categories'
      }
    },
    alerts: {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/alerts',
        requestDestination: '/ws/response/alerts'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/alerts/delete',
        requestDestination: '/ws/response/alerts/delete'
      }
    },
    'alerts-count': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/alerts/count',
        requestDestination: '/ws/response/alerts/count'
      }
    },
    'alerts-events': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/alerts/events',
        requestDestination: '/ws/response/alerts/events'
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
    },
    'milestone-types': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/options/investigation/milestone',
        requestDestination: '/ws/response/options/investigation/milestone'
      }
    },
    'remediation-tasks': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/remediation/tasks',
        requestDestination: '/ws/response/remediation/tasks'
      },
      query: {
        subscriptionDestination: '/user/queue/remediation/tasks',
        requestDestination: '/ws/response/remediation/tasks'
      },
      createRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/create',
        requestDestination: '/ws/response/remediation/tasks/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/update',
        requestDestination: '/ws/response/remediation/tasks/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/delete',
        requestDestination: '/ws/response/remediation/tasks/delete'
      }
    },
    'remediation-tasks-count': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/count',
        requestDestination: '/ws/response/remediation/tasks/count'
      }
    }
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  respondConfigGen,
  contextConfigGen
];

let socketConfig = null;

const generateSocketConfiguration = function(environment) {

  // this gets called a looooot on ember start up so use cache
  if (socketConfig) {
    return socketConfig;
  }

  socketConfig = common.mergeSocketConfigs(configGenerators, environment);

  // UNCOMMENT to see combined socketConfig on startup
  // console.log(socketConfig)

  return socketConfig;
};

module.exports = generateSocketConfiguration;
