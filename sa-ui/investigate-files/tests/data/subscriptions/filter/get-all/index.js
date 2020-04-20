import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/filter/get-all',
  requestDestination: '/ws/endpoint/filter/get-all',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
