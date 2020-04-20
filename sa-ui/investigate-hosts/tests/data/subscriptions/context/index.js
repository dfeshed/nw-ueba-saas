import data from './data';

export default {
  subscriptionDestination: '/user/queue/contexthub/context/lookup',
  requestDestination: '/ws/contexthub/context/lookup',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};