import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/process/list',
  requestDestination: '/ws/endpoint/process/list',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};


