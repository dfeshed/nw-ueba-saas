import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/filter/set',
  requestDestination: '/ws/endpoint/data/filter/set',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
