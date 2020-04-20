import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/filecontext/list',
  requestDestination: '/ws/endpoint/filecontext/list',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
