

var determineSocketUrl = require('../../../common').determineSocketUrl;

module.exports = function(environment) {

  var socketUrl = determineSocketUrl(environment, '/administration/socket');

  return {
    preferences: {
      socketUrl,
      getPreference: {
        subscriptionDestination: '/user/queue/administration/global/get/user/preferences',
        requestDestination: '/ws/administration/global/get/user/preferences'
      },
      setPreference: {
        subscriptionDestination: '/user/queue/administration/global/set/user/preferences',
        requestDestination: '/ws/administration/global/set/user/preferences'
      }
    },

    permissions: {
      socketUrl,
      getPermissions: {
        subscriptionDestination: '/user/queue/administration/rbac/get/permissions',
        requestDestination: '/ws/administration/rbac/get/permissions'
      }
    },

    timezones: {
      socketUrl,
      getTimezones: {
        subscriptionDestination: '/user/queue/administration/timezones/get',
        requestDestination: '/ws/administration/timezones/get'
      }
    },

    locales: {
      socketUrl,
      getLocales: {
        subscriptionDestination: '/user/queue/administration/locales/get',
        requestDestination: '/ws/administration/locales/get'
      }
    },

    passwords: {
      socketUrl,
      updatePassword: {
        subscriptionDestination: '/user/queue/administration/security/user/updatePassword',
        requestDestination: '/ws/administration/security/user/updatePassword'
      }
    },

    passwordPolicies: {
      socketUrl,
      getPolicies: {
        subscriptionDestination: '/user/queue/administration/security/password/policyMessages',
        requestDestination: '/ws/administration/security/password/policyMessages'
      }
    }
  };
};
