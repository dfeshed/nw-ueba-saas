import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/filter/getall',
  requestDestination: '/ws/endpoint/data/filter/getall',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
