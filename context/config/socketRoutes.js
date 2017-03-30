/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/administration/socket');

  return {
    context: {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/administration/context/lookup',
        requestDestination: '/ws/administration/context/lookup',
        cancelDestination: '/ws/administration/context/cancel'
      }
    },
    'list': {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/administration/context/list',
        requestDestination: '/ws/administration/context/list',
        cancelDestination: '/ws/administration/context/cancel'
      }
    },
    'save-entries':{
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/administration/context/list/save',
        requestDestination: '/ws/administration/context/list/save',
        cancelDestination: '/ws/administration/context/cancel'
      }
    },
    'create-list':{
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/administration/context/list/create',
        requestDestination: '/ws/administration/context/list/create',
        cancelDestination: '/ws/administration/context/cancel'
      }
    },
    'data-sources':{
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/administration/context/data-sources',
        requestDestination: '/ws/administration/context/data-sources',
        cancelDestination: '/ws/administration/context/cancel'
      }
    },
    'related-entity': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/administration/context/liveconnect/related',
        requestDestination: '/ws/administration/context/liveconnect/related'
      }
    },
    'liveconnect-feedback': {
      socketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/administration/context/liveconnect/feedback',
        requestDestination: '/ws/administration/context/liveconnect/feedback'
      }
    },
    'skill-level': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/administration/context/get/liveconnect/userprefs',
        requestDestination: '/ws/administration/context/get/liveconnect/userprefs'
      }
    }
  };
};