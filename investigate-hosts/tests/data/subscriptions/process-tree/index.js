import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/process/tree',
  requestDestination: '/ws/endpoint/data/process/tree',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};


