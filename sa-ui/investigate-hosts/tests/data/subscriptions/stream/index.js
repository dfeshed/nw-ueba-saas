import data from '../machines/data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/stream',
  requestDestination: '/ws/endpoint/machine/stream',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
