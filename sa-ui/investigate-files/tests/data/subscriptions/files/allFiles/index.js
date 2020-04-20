import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/file/search',
  requestDestination: '/ws/endpoint/file/search',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
