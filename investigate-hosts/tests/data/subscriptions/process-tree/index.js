import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/process/tree',
  requestDestination: '/ws/endpoint/process/tree',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};


