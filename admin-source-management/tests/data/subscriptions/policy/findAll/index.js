import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/policies',
  requestDestination: '/ws/usm/policies',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 12
      }
    };
  }
};
