import data from './data';

export default {
  subscriptionDestination: '/user/queue/content/parser/device/types',
  requestDestination: '/ws/content/parser/device/types',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
