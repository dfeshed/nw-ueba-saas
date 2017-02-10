import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/context/liveconnect/related',
  requestDestination: '/ws/administration/context/liveconnect/related',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
