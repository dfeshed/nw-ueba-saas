import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/process/list',
  requestDestination: '/ws/endpoint/machine/process/list',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};


