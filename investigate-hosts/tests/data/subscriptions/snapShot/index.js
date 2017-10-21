import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/machine/snapshots',
  requestDestination: '/ws/endpoint/data/machine/snapshots',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
