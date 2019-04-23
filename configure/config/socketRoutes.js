/* eslint-env node */

const common = require('../../common');

// As new microservices need to be used in this Configure engine, we'll need to adjust the socketUrl handling,
// but currently the only microservice in use is "respond"
const licenseConfigGen = require('../../license').socketRouteGenerator;

let mergedConfig;

const configureConfigGen = function(environment) {
  const socketUrl = common.determineSocketUrl(environment, '/respond/socket');
  const socketUrlLogs = common.determineSocketUrl(environment, '/content/socket');
  const socketUrlEndpoint = common.determineSocketUrl(environment, '/endpoint/socket');

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
    'risk-scoring-settings': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/risk/score/settings',
        requestDestination: '/ws/respond/risk/score/settings'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/risk/score/settings/update',
        requestDestination: '/ws/respond/risk/score/settings/update'
      }
    },
    'endpoint-server': {
      socketUrl: socketUrlEndpoint,
      findAll: {
        subscriptionDestination: '/user/queue/endpoint/server/get-all',
        requestDestination: '/ws/endpoint/server/get-all'
      }
    },
    filters: {
      socketUrl: socketUrlEndpoint,
      saveFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/set',
        requestDestination: '/ws/endpoint/filter/set'
      },
      getFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/get-all',
        requestDestination: '/ws/endpoint/filter/get-all'
      },
      deleteFilter: {
        subscriptionDestination: '/user/queue/endpoint/filter/remove',
        requestDestination: '/ws/endpoint/filter/remove'
      }
    }
  };
};


const generateSocketConfiguration = function(environment) {
  // cache it, prevents super spammy console as this gets called
  // many times during startup
  if (mergedConfig) {
    return mergedConfig;
  }

  // as of ember 2.14, for some reason environment can be undefined
  if (!environment) {
    return {};
  }

  const configGenerators = [configureConfigGen, licenseConfigGen];
  mergedConfig = common.mergeSocketConfigs(configGenerators, environment);
  return mergedConfig;
};

module.exports = generateSocketConfiguration;
