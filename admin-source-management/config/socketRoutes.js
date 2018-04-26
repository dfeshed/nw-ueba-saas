/* eslint-env node */

const common = require('../../common');

const adminUsmConfigGen = function(environment) {

  // As new microservices need to be used in this Admin engine, we'll need to adjust the socketUrl handling,
  const someSocketUrl = common.determineSocketUrl(environment, '/somePrefix/socket');

  return {
    /*someModelName: {
      socketUrl: someSocketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/somePrefix/someModel',
        requestDestination: '/ws/somePrefix/someModel'
      },
      queryRecord: {
        subscriptionDestination: '/user/queue/somePrefix/someModel/read',
        requestDestination: '/ws/somePrefix/someModel/read'
      },
      createRecord: {
        subscriptionDestination: '/user/queue/somePrefix/someModel/create',
        requestDestination: '/ws/somePrefix/someModel/create'
      },
      updateRecord: {
        subscriptionDestination: '/user/queue/somePrefix/someModel/update',
        requestDestination: '/ws/somePrefix/someModel/update'
      },
      deleteRecord: {
        subscriptionDestination: '/user/queue/somePrefix/someModel/delete',
        requestDestination: '/ws/somePrefix/someModel/delete'
      }
    }*/
  };
};

// order matters, first config in wins if there are matching configs
const configGenerators = [
  //adminUsmConfigGen
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