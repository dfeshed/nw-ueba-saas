import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/meta/keys/get-all',
  requestDestination: '/ws/investigate/meta/keys/get-all',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      data
    };
  }
};
