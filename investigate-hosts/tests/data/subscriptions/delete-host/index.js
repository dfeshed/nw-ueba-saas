import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/remove',
  requestDestination: '/ws/endpoint/machine/remove',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
