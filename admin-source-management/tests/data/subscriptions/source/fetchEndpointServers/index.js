import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/endpoint/servers',
  requestDestination: '/ws/usm/endpoint/servers',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 2
      }
    };
  }
};
