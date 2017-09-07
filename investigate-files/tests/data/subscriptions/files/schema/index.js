import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/files/schema',
  requestDestination: '/ws/endpoint/data/files/schema',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
