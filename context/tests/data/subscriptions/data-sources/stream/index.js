import data from './data';

export default {
  subscriptionDestination: '/user/queue/contexthub/context/data-sources',
  requestDestination: '/ws/contexthub/context/data-sources',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};