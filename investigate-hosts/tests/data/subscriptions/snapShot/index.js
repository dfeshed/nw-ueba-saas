import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/snapshots',
  requestDestination: '/ws/endpoint/machine/snapshots',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
