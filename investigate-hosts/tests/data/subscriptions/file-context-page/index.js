import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/filecontext/listpage',
  requestDestination: '/ws/endpoint/data/filecontext/listpage',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
