const common = require('../../common');
const contextConfigGen = require('../../context').socketRouteGenerator;

const respondSharedConfigGen = function(environment) {
  const socketUrl = common.determineSocketUrl(environment, '/respond/socket');
  const investigateSocketUrl = common.determineSocketUrl(environment, '/investigate/socket');

  return {
    'category-tags': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/categories',
        requestDestination: '/ws/respond/categories'
      }
    },
    'users': {
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
    'searched-incidents': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/incidents',
        requestDestination: '/ws/respond/incidents'
      }
    },
    'incident-events': {
      socketUrl: investigateSocketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/investigate/events/incident/create',
        requestDestination: '/ws/investigate/events/incident/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/investigate/events/incident/update',
        requestDestination: '/ws/investigate/events/incident/update'
      }
    },
    'incident-alerts': {
      socketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/incident/create',
        requestDestination: '/ws/respond/incident/create'
      }
    },
    'associated-alerts': {
      socketUrl,
      updateRecord: {
        subscriptionDestination: '/user/queue/alerts/associate',
        requestDestination: '/ws/respond/alerts/associate'
      }
    }
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  contextConfigGen,
  respondSharedConfigGen
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