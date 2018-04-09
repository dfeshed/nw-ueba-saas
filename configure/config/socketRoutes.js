/* eslint-env node */

const common = require('../../common');
const hostsScanConfiguration = require('../../hosts-scan-configure').socketRouteGenerator;
const configureConfigGen = function(environment) {

  // As new microservices need to be used in this Configure engine, we'll need to adjust the socketUrl handling,
  // but currently the only microservice in use is "respond"
  const socketUrl = common.determineSocketUrl(environment, '/respond/socket');
  const socketUrlLogs = common.determineSocketUrl(environment, '/content/socket');

  return {
    'parser-rules': {
      socketUrl: socketUrlLogs,
      findAllLogParsers: {
        subscriptionDestination: '/user/queue/content/parser/get',
        requestDestination: '/ws/content/parser/get'
      },
      getFormats: {
        subscriptionDestination: '/user/queue/content/parser/formats',
        requestDestination: '/ws/content/parser/formats'
      },
      getRules: {
        subscriptionDestination: '/user/queue/parser/rules',
        requestDestination: '/ws/logs/parser/rules'
      },
      queryRecord: {
        subscriptionDestination: '/user/queue/parser-rules/rule',
        requestDestination: '/ws/logs/parser-rules/rule'
      },
      createRecord: {
        subscriptionDestination: '/user/queue/parser-rules/rule/create',
        requestDestination: '/ws/logs/parser-rules/rule/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/parser-rules/rule/update',
        requestDestination: '/ws/logs/parser-rules/rule/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/parser-rules/rule/delete',
        requestDestination: '/ws/logs/parser-rules/rule/delete'
      },
      addRecord: {
        subscriptionDestination: '/user/queue/parser-rules/rule/add',
        requestDestination: '/ws/logs/parser-rules/rule/add'
      }
    },
    'category-tags': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/categories',
        requestDestination: '/ws/respond/categories'
      }
    },
    'incident-rule-clone': {
      socketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/alertrules/clone',
        requestDestination: '/ws/respond/alertrules/clone'
      }
    },
    'incident-rules-reorder': {
      socketUrl,
      updateRecord: {
        subscriptionDestination: '/user/queue/alertrules/reorder',
        requestDestination: '/ws/respond/alertrules/reorder'
      }
    },
    'incident-rules': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/alertrules',
        requestDestination: '/ws/respond/alertrules'
      },
      queryRecord: {
        subscriptionDestination: '/user/queue/alertrules/rule',
        requestDestination: '/ws/respond/alertrules/rule'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/alertrules/delete',
        requestDestination: '/ws/respond/alertrules/delete'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/alertrules/update',
        requestDestination: '/ws/respond/alertrules/update'
      },
      createRecord: {
        subscriptionDestination: '/user/queue/alertrules/create',
        requestDestination: '/ws/respond/alertrules/create'
      }
    },
    'incident-fields': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/alertrules/fields',
        requestDestination: '/ws/respond/alertrules/fields'
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
    'notification-settings': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/notifications',
        requestDestination: '/ws/respond/notifications'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/notifications/update',
        requestDestination: '/ws/respond/notifications/update'
      }
    }
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  configureConfigGen,
  hostsScanConfiguration
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
