import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/categories',
  requestDestination: '/ws/respond/categories',
  message() {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
