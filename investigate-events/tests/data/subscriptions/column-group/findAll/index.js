import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/column/groups/get',
  requestDestination: '/ws/investigate/column/groups/get',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      data
    };
  }
};
