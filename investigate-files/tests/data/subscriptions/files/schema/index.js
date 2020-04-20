import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/file/schema',
  requestDestination: '/ws/endpoint/file/schema',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
