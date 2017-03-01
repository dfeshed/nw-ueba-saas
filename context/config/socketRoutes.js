/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/administration/socket');

  // Want to run context panel inside sa
  // while sa still has respond and mirage in it?
  // Uncomment this line and comment the line above.
  //
  // const socketUrl = '/administration/socket';

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
    }
  };
};