/* eslint-env node */

const common = require('../../common');
const adminUsmConfigGen = function(environment) {

  // As new microservices need to be used in this Admin engine, we'll need to adjust the socketUrl handling,
  const usmSocketUrl = common.determineSocketUrl(environment, '/usm/socket');

  return {
    groups: {
      socketUrl: usmSocketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/usm/groups',
        requestDestination: '/ws/usm/groups'
      }/*,
      queryRecord: {
        subscriptionDestination: '/user/queue/usm/groups/read',
        requestDestination: '/ws/usm/groups/read'
      },
      createRecord: {
        subscriptionDestination: '/user/queue/usm/groups/create',
        requestDestination: '/ws/usm/groups/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/usm/groups/update',
        requestDestination: '/ws/usm/groups/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/usm/groups/delete',
        requestDestination: '/ws/usm/groups/delete'
      }*/
    }
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  adminUsmConfigGen
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
