import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/groups',
  requestDestination: '/ws/usm/groups',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 14
      }
    };
  }
};
