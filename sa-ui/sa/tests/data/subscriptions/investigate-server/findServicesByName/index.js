import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/services/name',
  requestDestination: '/ws/investigate/services/name',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
