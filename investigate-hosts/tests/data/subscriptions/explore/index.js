import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/filecontext/search',
  requestDestination: '/ws/endpoint/data/filecontext/search/stream',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
