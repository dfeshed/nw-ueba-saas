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

export const serviceData = [
  { 'id': '555d9a6fe4b0d37c827d402d', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR', 'version': '11.2.0.0', 'host': '10.4.61.33', 'port': 56005 },
  { 'id': '555d9a6fe4b0d37c827d4021', 'displayName': 'loki-broker', 'name': 'BROKER', 'version': '11.1.0.0', 'host': '10.4.61.28', 'port': 56003 },
  { 'id': '555d9a6fe4b0d37c827d402e', 'displayName': 'local-concentrator', 'name': 'CONCENTRATOR', 'version': '10.6.0.0', 'host': '127.0.0.1', 'port': 56005 },
  { 'id': '555d9a6fe4b0d37c827d402f', 'displayName': 'qamac01-concentrator', 'name': 'CONCENTRATOR', 'version': '11.1.0.0', 'host': '10.4.61.48', 'port': 56005 }
];