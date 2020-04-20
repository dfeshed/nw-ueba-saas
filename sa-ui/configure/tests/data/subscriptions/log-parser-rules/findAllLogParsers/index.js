import data from './data';

export default {
  subscriptionDestination: '/user/queue/content/parser/list',
  requestDestination: '/ws/content/parser/list',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
