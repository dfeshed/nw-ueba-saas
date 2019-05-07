import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/grouppolicy/get',
  requestDestination: '/ws/endpoint/grouppolicy/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
