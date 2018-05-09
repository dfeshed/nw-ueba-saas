import data from './data';

export default {
  subscriptionDestination: '/user/queue/content/parser/formats',
  requestDestination: '/ws/content/parser/formats',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
