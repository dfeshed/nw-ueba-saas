import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/detail',
  requestDestination: '/ws/endpoint/machine/detail',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};
