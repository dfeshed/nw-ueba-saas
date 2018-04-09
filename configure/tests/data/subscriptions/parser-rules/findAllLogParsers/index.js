import data from './data';

export default {
  subscriptionDestination: '/user/queue/content/parser/get',
  requestDestination: '/ws/content/parser/get',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 149
      }
    };
  }
};
