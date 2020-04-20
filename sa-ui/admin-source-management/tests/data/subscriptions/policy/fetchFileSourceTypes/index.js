import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/policy/file/types',
  requestDestination: '/ws/usm/policy/file/types',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 2
      }
    };
  }
};
