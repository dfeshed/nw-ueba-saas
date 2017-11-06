import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/process/get',
  requestDestination: '/ws/endpoint/process/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};
