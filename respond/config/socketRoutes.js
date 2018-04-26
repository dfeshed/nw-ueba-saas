/* eslint-env node */

const common = require('../../common');
const contextConfigGen = require('../../context').socketRouteGenerator;
const respondConfigGen = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/respond/socket');

  return {
    incidents: {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/incidents',
        requestDestination: '/ws/respond/incidents'
      },
      queryRecord: {
        subscriptionDestination: '/user/queue/incident/details',
        requestDestination: '/ws/respond/incident/details'
      },
      createRecord: {
        subscriptionDestination: '/user/queue/incident/create',
        requestDestination: '/ws/respond/incident/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/incidents/update',
        requestDestination: '/ws/respond/incidents/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/incidents/delete',
        requestDestination: '/ws/respond/incidents/delete'
      },
      escalate: {
        subscriptionDestination: '/user/queue/incident/escalate',
        requestDestination: '/ws/respond/incident/escalate'
      }
    },
    'incidents-settings': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/incidents/escalation/configuration',
        requestDestination: '/ws/respond/incidents/escalation/configuration'
      }
    },
    'incidents-count': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/incidents/count',
        requestDestination: '/ws/respond/incidents/count'
      }
    },
    storyline: {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/incident/storyline',
        requestDestination: '/ws/respond/incident/storyline'
      }
    },
    'category-tags': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/categories',
        requestDestination: '/ws/respond/categories'
      }
    },
    alerts: {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/alerts',
        requestDestination: '/ws/respond/alerts'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/alerts/delete',
        requestDestination: '/ws/respond/alerts/delete'
      }
    },
    'alerts-count': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/alerts/count',
        requestDestination: '/ws/respond/alerts/count'
      }
    },
    'alerts-events': {
      socketUrl,
      query: {
        subscriptionDestination: '/user/queue/alerts/events',
        requestDestination: '/ws/respond/alerts/events'
      }
    },
    'alerts-associated': {
      socketUrl,
      updateRecord: {
        subscriptionDestination: '/user/queue/alerts/associate',
        requestDestination: '/ws/respond/alerts/associate'
      }
    },
    'related-alerts-search': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/related/alerts',
        requestDestination: '/ws/respond/related/alerts'
      }
    },
    'original-alert': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/alerts/original',
        requestDestination: '/ws/respond/alerts/original'
      }
    },
    'journal-entry': {
      socketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/journal/create',
        requestDestination: '/ws/respond/journal/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/journal/update',
        requestDestination: '/ws/respond/journal/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/journal/delete',
        requestDestination: '/ws/respond/journal/delete'
      }
    },
    events: {
      socketUrl,
      stream: {
        defaultStreamLimit: 1000,
        subscriptionDestination: '/user/queue/alert/events',
        requestDestination: '/ws/respond/alert/events'
      }
    },
    users: {
      socketUrl,
      findAll: {
        defaultStreamLimit: 1000,
        subscriptionDestination: '/user/queue/users/all',
        requestDestination: '/ws/respond/users/all'
      }
    },
    'priority-types': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/options/priority',
        requestDestination: '/ws/respond/options/priority'
      }
    },
    'status-types': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/options/status',
        requestDestination: '/ws/respond/options/status'
      }
    },
    'escalation-statuses': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/incidents/escalationStatuses',
        requestDestination: '/ws/respond/incidents/escalationStatuses'
      }
    },
    'milestone-types': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/options/investigation/milestone',
        requestDestination: '/ws/respond/options/investigation/milestone'
      }
    },
    'remediation-tasks': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/remediation/tasks',
        requestDestination: '/ws/respond/remediation/tasks'
      },
      query: {
        subscriptionDestination: '/user/queue/remediation/tasks',
        requestDestination: '/ws/respond/remediation/tasks'
      },
      createRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/create',
        requestDestination: '/ws/respond/remediation/tasks/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/update',
        requestDestination: '/ws/respond/remediation/tasks/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/delete',
        requestDestination: '/ws/respond/remediation/tasks/delete'
      }
    },
    'remediation-tasks-count': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/remediation/tasks/count',
        requestDestination: '/ws/respond/remediation/tasks/count'
      }
    },
    'alert-names': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/alerts/distinct/names',
        requestDestination: '/ws/respond/alerts/distinct/names'
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

  // as of ember 2.14, for some reason environment can be undefined
  if (!environment) {
    return {};
  }

  socketConfig = common.mergeSocketConfigs(configGenerators, environment);

  // UNCOMMENT to see combined socketConfig on startup
  // console.log(socketConfig)

  return socketConfig;
};

module.exports = generateSocketConfiguration;
