import data from './data';

export default {
  subscriptionDestination: '/user/queue/contexthub/context/data-connections',
  requestDestination: '/ws/contexthub/context/data-connections',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};