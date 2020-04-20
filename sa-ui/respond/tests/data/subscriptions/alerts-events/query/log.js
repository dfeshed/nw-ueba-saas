export default [].concat(
  [
    {
      'action': '',
      'agent_id': '',
      'analysis_file': '',
      'analysis_service': '',
      'analysis_session': '',
      'category': 'Logon/Logoff',
      'data': [
        {
          'filename': '',
          'hash': '',
          'size': 641
        }
      ],
      'description': 'Successful',
      'destination': {
        'device': {
          'asset_type': '',
          'business_unit': '',
          'compliance_rating': '',
          'criticality': '',
          'facility': '',
          'geolocation': {
            'city': '',
            'country': '',
            'domain': '',
            'latitude': null,
            'longitude': null,
            'organization': ''
          },
          'ip_address': '',
          'mac_address': '',
          'netbios_name': '',
          'port': ''
        },
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': 'azkaislam'
        }
      },
      'destination_parameter': '',
      'detected_by': 'Windows Hosts-winevent_snare',
      'detector': {
        'device_class': 'Windows Hosts',
        'ip_address': '',
        'product_name': 'winevent_snare'
      },
      'device_type': 'winevent_snare',
      'domain': '09:50:16',
      'domain_dst': '',
      'domain_src': '',
      'enrichment': '',
      'event_source': '10.4.61.28:56003',
      'event_source_id': '154',
      'file': '',
      'file_SHA256': '',
      'from': '10.40.14.66',
      'host_dst': '',
      'host_src': '',
      'hostname': '09:50:16',
      'operating_system': '',
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/10.4.61.36:56005/navigate/event/AUTO/395871'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/10.4.61.36:56005/navigate/query/alias.host%3D"09%3A50%3A16"%2Fdate%2F2018-09-01T02%3A19%3A00.000Z%2F2018-09-01T02%3A29%3A00.000Z'
        }
      ],
      'size': 641,
      'source': {
        'device': {
          'asset_type': '',
          'business_unit': '',
          'compliance_rating': '',
          'criticality': '',
          'facility': '',
          'geolocation': {
            'city': '',
            'country': '',
            'domain': '',
            'latitude': null,
            'longitude': null,
            'organization': ''
          },
          'ip_address': '10.40.14.66',
          'mac_address': '',
          'netbios_name': '',
          'port': ''
        },
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': ''
        }
      },
      'source_filename': '',
      'source_hash': '',
      'source_parameter': '',
      'source_user_account': '',
      'target_filename': '',
      'target_hash': '',
      'timestamp': 1535768940000,
      'to': '',
      'type': 'Log',
      'user': 'azkaislam',
      'user_account': '',
      'user_dst': 'azkaislam',
      'user_src': '',
      'username': ''
    }
  ]
);
