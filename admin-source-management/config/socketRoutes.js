/* eslint-env node */

const common = require('../../common');
const adminUsmConfigGen = function(environment) {

  // As new microservices need to be used in this Admin engine, we'll need to adjust the socketUrl handling,
  const usmSocketUrl = common.determineSocketUrl(environment, '/usm/socket');

  return {
    groups: {
      socketUrl: usmSocketUrl,
      fetchGroups: {
        subscriptionDestination: '/user/queue/usm/groups/search',
        requestDestination: '/ws/usm/groups/search'
      },
      fetchGroupList: {
        subscriptionDestination: '/user/queue/usm/groups/list',
        requestDestination: '/ws/usm/groups/list'
      },
      fetchGroup: {
        subscriptionDestination: '/user/queue/usm/group/get',
        requestDestination: '/ws/usm/group/get'
      },
      fetchGroupRanking: {
        subscriptionDestination: '/user/queue/usm/group/rank/get-all',
        requestDestination: '/ws/usm/group/rank/get-all'
      },
      saveGroupRanking: {
        subscriptionDestination: '/user/queue/usm/group/rank/set',
        requestDestination: '/ws/usm/group/rank/set'
      },
      remove: {
        subscriptionDestination: '/user/queue/usm/groups/remove',
        requestDestination: '/ws/usm/groups/remove'
      },
      publish: {
        subscriptionDestination: '/user/queue/usm/groups/publish',
        requestDestination: '/ws/usm/groups/publish'
      },
      saveGroup: {
        subscriptionDestination: '/user/queue/usm/group/set',
        requestDestination: '/ws/usm/group/set'
      },
      savePublishRecord: {
        subscriptionDestination: '/user/queue/usm/group/saveandpublish',
        requestDestination: '/ws/usm/group/saveandpublish'
      },
      fetchRankingView: {
        subscriptionDestination: '/user/queue/usm/group/rank/effective-policy',
        requestDestination: '/ws/usm/group/rank/effective-policy'
      },
    },
    policy: {
      socketUrl: usmSocketUrl,
      fetchPolicies: {
        subscriptionDestination: '/user/queue/usm/policies/search',
        requestDestination: '/ws/usm/policies/search'
      },
      fetchPolicyList: {
        subscriptionDestination: '/user/queue/usm/policies/list',
        requestDestination: '/ws/usm/policies/list'
      },
      fetchPolicy: {
        subscriptionDestination: '/user/queue/usm/policy/get',
        requestDestination: '/ws/usm/policy/get'
      },
      remove: {
        subscriptionDestination: '/user/queue/usm/policies/remove',
        requestDestination: '/ws/usm/policies/remove'
      },
      publish: {
        subscriptionDestination: '/user/queue/usm/policies/publish',
        requestDestination: '/ws/usm/policies/publish'
      },
      savePolicy: {
        subscriptionDestination: '/user/queue/usm/policy/set',
        requestDestination: '/ws/usm/policy/set'
      },
      savePublishRecord: {
        subscriptionDestination: '/user/queue/usm/policy/saveandpublish',
        requestDestination: '/ws/usm/policy/saveandpublish'
      },
      fetchEndpointServers: {
        subscriptionDestination: '/user/queue/usm/endpoint/servers',
        requestDestination: '/ws/usm/endpoint/servers'
      },
      fetchLogServers: {
        subscriptionDestination: '/user/queue/usm/log/servers',
        requestDestination: '/ws/usm/log/servers'
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
