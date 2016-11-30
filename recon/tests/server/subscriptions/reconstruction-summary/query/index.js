import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-summary',
  requestDestination: '/ws/investigate/reconstruct/session-summary',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};