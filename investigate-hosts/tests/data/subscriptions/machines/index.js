import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/search',
  requestDestination: '/ws/endpoint/machine/search',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
