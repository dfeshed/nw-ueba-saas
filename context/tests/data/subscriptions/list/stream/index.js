import data from './data';

export default {
  subscriptionDestination: '/user/queue/contexthub/context/list',
  requestDestination: '/ws/contexthub/context/list',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
