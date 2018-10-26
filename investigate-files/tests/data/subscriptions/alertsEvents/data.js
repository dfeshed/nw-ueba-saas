export default [].concat(
  [
    {
      'agent_id': 'C73CD5FF-5962-4A5F-2E9D-1CFFF4DFED2D',
      'data': [{ 'filename': '', 'size': 41, 'hash': '' }],
      'target_hash': '6bd1f5ab9250206ab3836529299055e272ecaa35a72cbd0230cb20ff1cc30902',
      'destination':
      {
        'device':
        {
          'compliance_rating': '',
          'netbios_name': '',
          'port': '',
          'mac_address': '',
          'criticality': '',
          'asset_type': '',
          'ip_address': '',
          'facility': '',
          'business_unit': '',
          'geolocation':
          {
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user':
        {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'ip_src': '',
      'description': '',
      'domain_src': '',
      'device_type': 'nwendpoint',
      'event_source': '10.40.15.182:50002',
      'source': {
        'device': {
          'compliance_rating': '',
          'netbios_name': '',
          'port': '',
          'mac_address': '',
          'criticality': '',
          'asset_type': '',
          'ip_address': '',
          'facility': '',
          'business_unit': '',
          'geolocation': {
            'country': '', 'city': '', 'latitude': null, 'organization': '', 'domain': '', 'longitude': null
          }
        },
        'user': {
          'email_address': '', 'ad_username': '', 'ad_domain': '', 'username': 'WIN2012R2TMPLTE\\\\Administrator'
        }
      },
      'type': 'Log',
      'analysis_file': '',
      'launch_argument_src': 'POWERSHELL.EXE [System.Text.Encoding]::Unicode.GetString([System.Convert]::FromBase64String("YmxhaGJsYWg="))',
      'enrichment': '',
      'user_src': 'WIN2012R2TMPLTE\\\\Administrator',
      'hostname': 'WIN2012R2TMPLTE',
      'analysis_service': '',
      'file': '',
      'detected_by': '-nwendpoint',
      'source_filename': 'POWERSHELL.EXE',
      'target_filename': 'Conhost.exe',
      'host_src': '',
      'action': 'createProcess',
      'operating_system': 'windows',
      'from': '',
      'source_hash': '840e1f9dc5a29bebf01626822d7390251e9cf05bb3560ba7b68bdb8a41cf08e3',
      'timestamp': '2018-10-23T13:18:02.000+0000',
      'event_source_id': '14688',
      'related_links': [{
        'type': 'investigate_original_event', 'url': '/investigation/host/10.40.15.182:50002/navigate/event/AUTO/14688'
      },
      {
        'type': 'investigate_destination_domain',
        'url': '/investigation/10.40.15.182:50002/navigate/query/alias.host%3D"WIN2012R2TMPLTE"%2Fdate%2F2018-10-23T13%3A08%3A02.000Z%2F2018-10-23T13%3A18%3A02.000Z'
      }],
      'port_dst': '',
      'domain_dst': '',
      'target_path': 'C:\\\\Windows\\\\System32\\\\',
      'ip_dst': '',
      'launch_argument_dst': 'conhost.exe 0xffffffff',
      'file_SHA256': '6bd1f5ab9250206ab3836529299055e272ecaa35a72cbd0230cb20ff1cc30902',
      'user_dst': '',
      'host_dst': '',
      'size': 41,
      'domain': 'WIN2012R2TMPLTE',
      'user_account': '',
      'source_path': 'C:\\\\Windows\\\\system32\\\\WindowsPowerShell\\\\v1.0\\\\',
      'to': '',
      'category': 'Process Event',
      'detector': { 'device_class': '', 'ip_address': '', 'product_name': 'nwendpoint' },
      'user': 'WIN2012R2TMPLTE\\\\Administrator',
      'analysis_session': '',
      'username': ''
    }
  ]
);