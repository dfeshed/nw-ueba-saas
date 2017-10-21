import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/process/list',
  requestDestination: '/ws/endpoint/data/process/list',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};


