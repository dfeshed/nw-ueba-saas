import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/machine/schema',
  requestDestination: '/ws/endpoint/data/machine/schema',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
