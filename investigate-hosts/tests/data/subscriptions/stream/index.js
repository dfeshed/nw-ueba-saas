import data from '../machines/data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/machine/stream',
  requestDestination: '/ws/endpoint/data/machine/stream',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
