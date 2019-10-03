import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/profile/get-all',
  requestDestination: '/ws/investigate/profile/get-all',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      data
    };
  }
};
