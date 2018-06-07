const sessionNotFound = {
  'code': 1000,
  'meta': {
    'message': 'The remote content for session 1 on device \'logdecoderi\' is no longer available'
  },
  'request': {
    'filter': [
      {
        'field': 'endpointId',
        'isNull': false,
        'operator': '==',
        'value': 'b103f57c-ed1a-4862-aa53-e30687f130b3'
      },
      {
        'field': 'sessionId',
        'isNull': false,
        'operator': '==',
        'value': '99719'
      }
    ],
    'filterOperator': 'and',
    'id': 'req-5',
    'sort': [],
    'stream': {
      'batch': 100,
      'limit': 100000
    }
  }
};

export { sessionNotFound };
