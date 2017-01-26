import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/aliases',
  requestDestination: '/ws/investigate/aliases',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};