import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/files/search',
  requestDestination: '/ws/endpoint/data/files/search',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
