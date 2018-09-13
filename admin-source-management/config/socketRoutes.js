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
      },
      remove: {
        subscriptionDestination: '/user/queue/usm/groups/remove',
        requestDestination: '/ws/usm/groups/remove'
      },
      publish: {
        subscriptionDestination: '/user/queue/usm/groups/publish',
        requestDestination: '/ws/usm/groups/publish'
      },
      getGroup: {
        subscriptionDestination: '/user/queue/usm/group/get',
        requestDestination: '/ws/usm/group/get'
      },
      saveGroup: {
        subscriptionDestination: '/user/queue/usm/group/set',
        requestDestination: '/ws/usm/group/set'
      },
      savePublishRecord: {
        subscriptionDestination: '/user/queue/usm/group/saveandpublish',
        requestDestination: '/ws/usm/group/saveandpublish'
      }
    },
    policy: {
      socketUrl: usmSocketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/usm/policies',
        requestDestination: '/ws/usm/policies'
      },
      remove: {
        subscriptionDestination: '/user/queue/usm/policies/remove',
        requestDestination: '/ws/usm/policies/remove'
      },
      publish: {
        subscriptionDestination: '/user/queue/usm/policies/publish',
        requestDestination: '/ws/usm/policies/publish'
      },
      getPolicy: {
        subscriptionDestination: '/user/queue/usm/policy/get',
        requestDestination: '/ws/usm/policy/get'
      },
      savePolicy: {
        subscriptionDestination: '/user/queue/usm/policy/set',
        requestDestination: '/ws/usm/policy/set'
      },
      savePublishRecord: {
        subscriptionDestination: '/user/queue/usm/policy/saveandpublish',
        requestDestination: '/ws/usm/policy/saveandpublish'
      }
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
