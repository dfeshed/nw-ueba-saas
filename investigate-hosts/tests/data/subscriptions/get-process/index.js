import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/process/get',
  requestDestination: '/ws/endpoint/data/process/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};
