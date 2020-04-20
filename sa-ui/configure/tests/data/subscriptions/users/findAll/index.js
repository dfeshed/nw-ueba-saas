import data from './data';

export default {
  subscriptionDestination: '/user/queue/users/all',
  requestDestination: '/ws/respond/users/all',
  message(/* frame */) {
    return {
      data,
      meta: {
        total: data.length
      }
    };
  }
};
