export const invalidServerResponse = {
  'code': 1,
  'request': {
    'id': 'req-12',
    'stream': {
      'limit': 100000,
      'batch': 100
    },
    'sort': [],
    'filter': [
      {
        'field': 'endpointId',
        'value': '2',
        'operator': '==',
        'isNull': false
      },
      {
        'field': 'query',
        'value': 'sessionid%20%3D%20242424242424242424242424',
        'operator': '==',
        'isNull': false
      }
    ],
    'filterOperator': 'and'
  },
  'meta': {
    'message': 'expecting <comma-separated list of numeric ranges, values, or value aliases> or <comma-separated list of keys> here: \'242424242424242424242424\''
  }
};

export const invalidServerResponseText = {
  'code': 1,
  'request': {
    'id': 'req-12',
    'stream': {
      'limit': 100000,
      'batch': 100
    },
    'sort': [],
    'filter': [
      {
        'field': 'endpointId',
        'value': '2',
        'operator': '==',
        'isNull': false
      },
      {
        'field': 'query',
        'value': 'alert%20%3D%20x',
        'operator': '==',
        'isNull': false
      }
    ],
    'filterOperator': 'and'
  },
  'meta': {
    'message': 'Invalid server response'
  }
};