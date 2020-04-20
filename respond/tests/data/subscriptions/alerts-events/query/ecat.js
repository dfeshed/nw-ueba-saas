export default [].concat(
  [
    {
      'data': [
        {
          'bit9_status': 'bad',
          'filename': 'AppIdPolicyEngineApi.dll',
          'hash': 'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3',
          'module_signature': 'ABC Inc.',
          'opswat_result': 'OPSWAT result here',
          'size': '23562',
          'yara_result': 'N YARA rules matched'
        }
      ],
      'description': 'ModuleIOC',
      'detected_by': 'it_laptop1.eng.matrix.com',
      'detector': {
        'dns_hostname': 'it_laptop1.eng.matrix.com',
        'ecat_agent_id': '26C5C21F-4DA8-3A00-437C-AB7444987430',
        'ip_address': '100.3.36.242',
        'mac_address': 'B8-4B-2F-08-6A-AD-5A-C7',
        'os': 'Windows 7'
      },
      'file': 'AppIdPolicyEngineApi.dll',
      'score': '1-2-3-4',
      'size': '23562',
      'timestamp': 1371725338000,
      'type': 'ecat event',
      'user': ''
    }
  ]
);
