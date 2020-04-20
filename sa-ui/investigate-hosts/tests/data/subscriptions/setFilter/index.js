import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/filter/set',
  requestDestination: '/ws/endpoint/filter/set',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
