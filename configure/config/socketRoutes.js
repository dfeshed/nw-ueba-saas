/* eslint-env node */

const common = require('../../common');
const hostsScanConfiguration = require('../../hosts-scan-configure').socketRouteGenerator;
const configureConfigGen = function(environment) {

  // As new microservices need to be used in this Configure engine, we'll need to adjust the socketUrl handling,
  // but currently the only microservice in use is "respond"
  const socketUrl = common.determineSocketUrl(environment, '/respond/socket');

  return {
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
