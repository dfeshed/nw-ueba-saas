import data from './data';

export default {
  delay: 1,
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
