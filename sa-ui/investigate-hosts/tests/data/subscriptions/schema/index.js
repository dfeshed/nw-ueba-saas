import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/schema',
  requestDestination: '/ws/endpoint/machine/schema',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
