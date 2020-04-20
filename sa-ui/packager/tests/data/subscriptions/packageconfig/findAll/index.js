import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/server/get-all',
  requestDestination: '/ws/endpoint/server/get-all',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
