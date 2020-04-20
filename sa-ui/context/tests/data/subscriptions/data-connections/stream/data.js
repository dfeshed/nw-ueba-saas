export default [
  {
    'createdBy': 'Netwitness Web(nw-web)',
    'createdOn': 1534407674014,
    'lastModifiedBy': 'Netwitness Web(nw-web)',
    'lastModifiedOn': 1535625514478,
    'id': '5b82f6eeb3e9d75d78a9183f',
    'name': 'LiveConnect',
    'type': 'LiveConnect',
    'enabled': true,
    'connectionParameters': {
      'Max Concurrent Queries': 5,
      'Username': 'Kaylan.Juthada',
      'Https': true,
      'Port': 443,
      'Host': 'cms.netwitness.com',
      'Password': ''
    },
    'transport': 'HttpTransportTokenAuth'
  },
  {
    'createdBy': 'system',
    'createdOn': 1534411416293,
    'lastModifiedBy': 'Netwitness Web(nw-web)',
    'lastModifiedOn': 1535622914065,
    'id': '5b754298b8880c4ddd15deb1',
    'name': 'FileReputationServer',
    'type': 'FileReputationServer',
    'enabled': true,
    'connectionParameters': {
      'test_conn_http_method': 'POST',
      'auth_enabled': true,
      'Https': true,
      'test_conn_http_body': '{\'hashType\': \'md5\', \'hashes\': [ \'11111\']}',
      'Port': 443,
      'autoConfigured': true,
      'Host': 'cms.test.netwitness.com',
      'auth_header_username': 'X-Auth-Username',
      'auth_header_password': 'X-Auth-Password',
      'auth_header_token': 'X-Auth-Token',
      'auth_http_headers': '{\'Content-Type\':\'application/json\'}',
      'test_conn_url_path': '/mwp/query?extended=true',
      'test_conn_http_headers': '{\'Content-Type\':\'application/json\'}',
      'auth_url_path': '/authlive/authenticate/RL',
      'Username': 'Kaylan.Juthada',
      'verify_ssl_certificate': true,
      'Password': '',
      'auth_http_method': 'POST'
    },
    'transport': 'HttpTransportTokenAuth'
  },
  {
    'createdBy': 'admin',
    'createdOn': 1535030014962,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1535030014962,
    'id': '5b890b2e65d35255ca40e806',
    'name': 'AdminServer - Respond Server',
    'type': 'Respond',
    'enabled': true,
    'connectionParameters': {
      'Service': 'AdminServer - Respond Server',
      'Max Concurrent Queries': 10,
      'ServiceDatabase': 'respond-server'
    },
    'transport': 'MongoTransport'
  },
  {
    'createdBy': 'Netwitness Web(nw-web)',
    'createdOn': 1534407674014,
    'lastModifiedBy': 'Netwitness Web(nw-web)',
    'lastModifiedOn': 1535625514478,
    'id': '5b82f6eeb3e9d75d78a9183h',
    'name': 'Archer',
    'type': 'Archer',
    'enabled': true,
    'connectionParameters': {
      'Max Concurrent Queries': 5,
      'Username': 'Kaylan.Juthada',
      'Https': true,
      'Port': 443,
      'Host': 'cms.netwitness.com',
      'Password': ''
    },
    'transport': 'HttpTransportTokenAuth'
  }
];
