import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/filecontext/search',
  requestDestination: '/ws/endpoint/filecontext/search/stream',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
