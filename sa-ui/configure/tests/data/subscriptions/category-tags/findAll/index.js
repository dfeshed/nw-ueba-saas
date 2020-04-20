import data from './data';

export default {
  subscriptionDestination: '/user/queue/categories',
  requestDestination: '/ws/respond/categories',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
