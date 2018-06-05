export default [{
  'Archer': {
    'dataSourceType': 'Archer',
    'dataSourceGroup': 'Archer',
    'connectionName': 'test',
    'warning': {
      'data': 'report1 , qwerty'
    },
    'resultList': [
      {
        'Device Owner': '',
        'Business Unit': '',
        'Host Name': 'NewHost',
        'MAC Address': '',
        'internal_pivot_archer_request_url': 'HTTPS://10.31.204.245/RSAArcher/default.aspx?requestUrl=..%2fGenericContent%2fRecord.aspx%3fid%3d324945%26moduleId%3d71',
        'Facilities': '',
        'Risk Rating': '',
        'IP Address': '10.30.91.91',
        'Type': 'Desktop',
        'Device ID': '324945',
        'Device Name': 'New Device',
        'Criticality Rating': 'Not Rated',
        'Business Processes': [ 'Process 1', 'Process 2', 'Process 3', 'Process 4' ]
      }
    ],
    'order': [ 'Criticality Rating', 'Risk Rating', 'Device Name', 'Host Name', 'IP Address', 'Device ID', 'Type', 'MAC Address', 'Facilities', 'Business Unit', 'Device Owner', 'internal_pivot_archer_request_url' ]
  }
}];