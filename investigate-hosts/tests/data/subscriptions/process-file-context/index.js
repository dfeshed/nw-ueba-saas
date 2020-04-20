import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/process/get-all',
  requestDestination: '/ws/endpoint/machine/process/get-all',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};


