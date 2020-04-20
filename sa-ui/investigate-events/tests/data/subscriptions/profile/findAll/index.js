import profiles from '..';

export default {
  subscriptionDestination: '/user/queue/investigate/profile/get-all',
  requestDestination: '/ws/investigate/profile/get-all',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      data: profiles
    };
  }
};
