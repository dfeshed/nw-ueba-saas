import data from './data';

export default {
  subscriptionDestination: '/user/queue/categories',
  requestDestination: '/ws/response/categories',
  message(/* frame */) {
    return {
      'code': 0,
      data,
      'request': {
        'id': 'req-5',
        'stream': {
          'limit': 100000,
          'batch': 100
        },
        'sort': [],
        'filter': []
      },
      'meta': {
        'total': 149
      }
    };
  }
};
