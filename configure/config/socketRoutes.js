/* eslint-env node */

const common = require('../../common');
const configureConfigGen = function(environment) {

  // As new microservices need to be used in this Configure engine, we'll need to adjust the socketUrl handling,
  // but currently the only microservice in use is "respond"
  const socketUrl = common.determineSocketUrl(environment, '/respond/socket');
  const socketUrlLogs = common.determineSocketUrl(environment, '/content/socket');
  const socketUrlEndpoint = common.determineSocketUrl(environment, '/endpoint/socket');
  const contextSocketUrl = common.determineSocketUrl(environment, '/contexthub/socket');

  return {
    'log-parser-rules': {
      socketUrl: socketUrlLogs,
      findAllLogParsers: {
        subscriptionDestination: '/user/queue/content/parser/list',
        requestDestination: '/ws/content/parser/list'
      },
      fetchRuleFormats: {
        subscriptionDestination: '/user/queue/content/parser/formats',
        requestDestination: '/ws/content/parser/formats'
      },
      fetchParserRules: {
        subscriptionDestination: '/user/queue/content/parser/rules',
        requestDestination: '/ws/content/parser/rules'
      },
      deployLogParser: {
        subscriptionDestination: '/user/queue/content/parser/deploy',
        requestDestination: '/ws/content/parser/deploy'
      },
      saveParserRule: {
        subscriptionDestination: '/user/queue/content/parser/rules/update',
        requestDestination: '/ws/content/parser/rules/update'
      },
      update: {
        subscriptionDestination: '/user/queue/content/parser/update',
        requestDestination: '/ws/content/parser/update'
      },
      highlight: {
        subscriptionDestination: '/user/queue/content/parser/highlight',
        requestDestination: '/ws/content/parser/highlight'
      }
    },
    'device-types': {
      socketUrl: socketUrlLogs,
      findAll: {
        subscriptionDestination: '/user/queue/content/parser/device/types',
        requestDestination: '/ws/content/parser/device/types'
      }
    },
    'device-classes': {
      socketUrl: socketUrlLogs,
      findAll: {
        subscriptionDestination: '/user/queue/content/parser/device/class',
        requestDestination: '/ws/content/parser/device/class'
      }
    },
    'rule-metas': {
      socketUrl: socketUrlLogs,
      findAll: {
        subscriptionDestination: '/user/queue/content/parser/metas',
        requestDestination: '/ws/content/parser/metas'
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
    },
    'endpoint-certificates': {
      socketUrl: socketUrlEndpoint,
      getCertificates: {
        subscriptionDestination: '/user/queue/endpoint/certificate/search',
        requestDestination: '/ws/endpoint/certificate/search'
      }
    },
    'context-data': {
      socketUrl: contextSocketUrl,
      setCertificateStatus: {
        subscriptionDestination: '/user/queue/contexthub/certificate/status/set',
        requestDestination: '/ws/contexthub/certificate/status/set'
      },
      getCertificateStatus: {
        subscriptionDestination: '/user/queue/contexthub/context/data-source/find',
        requestDestination: '/ws/contexthub/context/data-source/find'
      }
    },
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  configureConfigGen
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
