import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/springboard/all',
  requestDestination: '/ws/administration/springboard/all',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};