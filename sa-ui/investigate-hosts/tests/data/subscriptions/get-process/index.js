import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/machine/process/get',
  requestDestination: '/ws/endpoint/machine/process/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }

};
