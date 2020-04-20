import data from './data';

export default {
  subscriptionDestination: '/user/queue/contexthub/context/liveconnect/related',
  requestDestination: '/ws/contexthub/context/liveconnect/related',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
