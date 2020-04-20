import data from './data';

export default {
  delay: 1,
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
