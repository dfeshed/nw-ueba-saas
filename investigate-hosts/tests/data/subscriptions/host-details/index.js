import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/machine/detail',
  requestDestination: '/ws/endpoint/data/machine/detail',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};
