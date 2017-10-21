import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/machine/search',
  requestDestination: '/ws/endpoint/data/machine/search',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
