import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-files',
  requestDestination: '/ws/investigate/reconstruct/session-files',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

