import data from './data';

export default {
  subscriptionDestination: '/user/queue/content/parser/rules',
  requestDestination: '/ws/content/parser/rules',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
