/* eslint-env node */

const common = require('../../common');

module.exports = function(environment) {

  const socketUrl = common.determineSocketUrl(environment, '/contexthub/socket');

  return {
    context: {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/contexthub/context/lookup',
        requestDestination: '/ws/contexthub/context/lookup',
        cancelDestination: '/ws/contexthub/context/cancel'
      }
    },
    'list': {
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/contexthub/context/list',
        requestDestination: '/ws/contexthub/context/list',
        cancelDestination: '/ws/contexthub/context/cancel'
      }
    },
    'save-entries':{
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/contexthub/context/list/save',
        requestDestination: '/ws/contexthub/context/list/save',
        cancelDestination: '/ws/contexthub/context/cancel'
      }
    },
    'create-list':{
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/contexthub/context/list/create',
        requestDestination: '/ws/contexthub/context/list/create',
        cancelDestination: '/ws/contexthub/context/cancel'
      }
    },
    'data-sources':{
      socketUrl,
      stream: {
        defaultStreamLimit: 100000,
        subscriptionDestination: '/user/queue/contexthub/context/data-sources',
        requestDestination: '/ws/contexthub/context/data-sources',
        cancelDestination: '/ws/contexthub/context/cancel'
      }
    },
    'related-entity': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/contexthub/context/liveconnect/related',
        requestDestination: '/ws/contexthub/context/liveconnect/related'
      }
    },
    'liveconnect-feedback': {
      socketUrl,
      createRecord: {
        subscriptionDestination: '/user/queue/contexthub/context/liveconnect/feedback',
        requestDestination: '/ws/contexthub/context/liveconnect/feedback'
      }
    },
    'skill-level': {
      socketUrl,
      queryRecord: {
        subscriptionDestination: '/user/queue/contexthub/context/get/liveconnect/userprefs',
        requestDestination: '/ws/contexthub/context/get/liveconnect/userprefs'
      }
    },
    'entity-type': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/contexthub/context/types',
        requestDestination: '/ws/contexthub/context/types'
      }
    },
    'entity-summary': {
      socketUrl,
      stream: {
        subscriptionDestination: '/user/queue/contexthub/context/flagging',
        requestDestination: '/ws/contexthub/context/flagging',
        cancelDestination: '/ws/contexthub/context/cancel'
      }
    },
    'entity-meta': {
      socketUrl,
      findAll: {
        subscriptionDestination: '/user/queue/contexthub/context/metas',
        requestDestination: '/ws/contexthub/context/metas'
      }
    }
  };
};