import data from './data';

export default {
  subscriptionDestination: '/user/queue/springboard/all',
  requestDestination: '/ws/springboard/all',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};