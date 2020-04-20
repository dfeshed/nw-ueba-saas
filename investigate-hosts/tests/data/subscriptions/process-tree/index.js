import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/process/tree',
  requestDestination: '/ws/endpoint/machine/process/tree',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};


