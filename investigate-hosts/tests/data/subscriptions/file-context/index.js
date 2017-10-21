import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/filecontext/list',
  requestDestination: '/ws/endpoint/data/filecontext/list',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
