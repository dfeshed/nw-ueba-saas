import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/log/servers',
  requestDestination: '/ws/usm/log/servers',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 2
      }
    };
  }
};
