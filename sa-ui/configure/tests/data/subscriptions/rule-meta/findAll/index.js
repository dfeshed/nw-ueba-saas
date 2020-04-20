import data from './data';

export default {
  subscriptionDestination: '/user/queue/content/parser/metas',
  requestDestination: '/ws/content/parser/metas',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
