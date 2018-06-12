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

const nextgenException = {
  'code': 1,
  'request': {
    'id': 'req-5',
    'stream': {
      'limit': 100000,
      'batch': 100
    },
    'sort': [],
    'filter': [
      {
        'field': 'endpointId',
        'value': '0f9da009-2da7-4a97-ad46-7914c6787359',
        'operator': '==',
        'isNull': false
      },
      {
        'field': 'sessionId',
        'value': '1',
        'operator': '==',
        'isNull': false
      }
    ],
    'filterOperator': 'and'
  },
  'meta': {
    'message': 'com.rsa.asoc.transport.nw.session.NextgenException: The remote content for session 1 on device \'decoderi\' is no longer available'
  }
};

export { sessionNotFound, nextgenException };
