import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/filecontext/list-page',
  requestDestination: '/ws/endpoint/filecontext/list-page',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
