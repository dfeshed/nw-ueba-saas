export default [].concat(
  [
    {
      'action': 'createProcess',
      'agent_id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'alias_host': 'WIN7ENTX64',
      'analysis_file': '',
      'analysis_service': '',
      'analysis_session': '',
      'category': 'Process Event',
      'data': [
        {
          'filename': 'test_filename',
          'hash': 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
          'size': 41
        }
      ],
      'description': 'test_event_description',
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
        'filename': 'cmd.EXE',
        'hash': '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
        'launch_argument': 'cmd.EXE /C COPY /Y C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\dtf.exe C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\MSHTA.EXE',
        'path': 'C:\\WINDOWS\\System32\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': ''
        }
      },
      'detected_by': 'Windows Hosts-nwendpoint,10.6.66.141',
      'detector': {
        'device_class': 'Windows Hosts',
        'ip_address': '10.6.66.141',
        'product_name': 'nwendpoint'
      },
      'device_type': 'nwendpoint',
      'domain': 'INENMENONS4L2C',
      'domain_dst': 'nist.gov',
      'domain_src': 'corp.rsa',
      'enrichment': '',
      'event_source': '10.63.0.117:56005',
      'event_source_id': '857775',
      'file': 'test_filename',
      'from': 'test_ad_computer_src:21',
      'host_dst': 'test_host_dst',
      'host_src': 'test_host_src',
      'hostname': 'INENMENONS4L2C',
      'operating_system': 'windows',
      'port_dst': '',
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857775'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
        }
      ],
      'size': 41,
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
          'ip_address': '',
          'mac_address': '',
          'netbios_name': '',
          'port': ''
        },
        'filename': 'dtf.exe',
        'hash': '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
        'launch_argument': 'dtf.exe  -dll:ioc.dll -testcase:353',
        'path': 'C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': 'CORP\\menons4'
        }
      },
      'timestamp': 1528429212000,
      'to': '',
      'type': 'Endpoint',
      'user': 'CORP\\menons4',
      'user_account': '',
      'user_dst': '',
      'user_src': 'CORP\\menons4',
      'username': ''
    },
    {
      'action': 'openProcess',
      'agent_id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'alias_host': 'WIN7ENTX64',
      'analysis_file': '',
      'analysis_service': '',
      'analysis_session': '',
      'category': 'Process Event',
      'data': [
        {
          'filename': '',
          'hash': '',
          'size': 41
        }
      ],
      'description': '',
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
        'filename': 'cmd.EXE',
        'hash': '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
        'launch_argument': 'PowerShell.exe --run',
        'path': 'C:\\WINDOWS\\System\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': ''
        }
      },
      'detected_by': '-nwendpoint',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': 'nwendpoint'
      },
      'device_type': 'nwendpoint',
      'domain': 'INENMENONS4L2C',
      'domain_dst': '',
      'domain_src': '',
      'enrichment': '',
      'event_source': '10.63.0.117:56005',
      'event_source_id': '857776',
      'file': '',
      'from': '',
      'host_dst': '',
      'host_src': '',
      'hostname': 'INENMENONS4L2C',
      'operating_system': 'windows',
      'port_dst': '',
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857776'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
        }
      ],
      'size': 41,
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
          'ip_address': '',
          'mac_address': '',
          'netbios_name': '',
          'port': ''
        },
        'filename': 'dtf.exe',
        'hash': '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
        'launch_argument': 'dtf.exe  -dll:ioc.dll -testcase:353',
        'path': 'C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd32\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': 'CORP\\menons4'
        }
      },
      'timestamp': 1528429212000,
      'to': '',
      'type': 'Endpoint',
      'user': 'CORP\\menons4',
      'user_account': 'foobar',
      'user_dst': '',
      'user_src': 'CORP\\menons4',
      'username': ''
    },
    {
      'action': 'createProcess',
      'agent_id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'alias_host': 'WIN7ENTX64',
      'analysis_file': '',
      'analysis_service': '',
      'analysis_session': '',
      'category': 'Process Event',
      'data': [
        {
          'filename': '',
          'hash': '',
          'size': 41
        }
      ],
      'description': '',
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
        'filename': 'cmd.exe',
        'hash': '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
        'launch_argument': 'cmd.exe  /C sc stop dtfsvc && sc delete dtfsvc',
        'path': 'C:\\WINDOWS\\System42\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': ''
        }
      },
      'detected_by': '-nwendpoint',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': 'nwendpoint'
      },
      'device_type': 'nwendpoint',
      'domain': 'INENMENONS4L2C',
      'domain_dst': '',
      'domain_src': '',
      'enrichment': '',
      'event_source': '10.63.0.117:56005',
      'event_source_id': '857782',
      'file': '',
      'from': '',
      'host_dst': '',
      'host_src': '',
      'hostname': 'LINUXHOSTNAME',
      'operating_system': 'linux',
      'port_dst': '',
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857782'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
        }
      ],
      'size': 41,
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
          'ip_address': '',
          'mac_address': '',
          'netbios_name': '',
          'port': ''
        },
        'filename': 'dtf.exe',
        'hash': '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
        'launch_argument': 'dtf.exe  -dll:ioc.dll -testcase:353',
        'path': 'C:\\WINDOWS\\System23\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': 'CORP\\menons4'
        }
      },
      'timestamp': 1528429212000,
      'to': '',
      'type': 'Endpoint',
      'user': 'CORP\\menons4',
      'user_account': '',
      'user_dst': '',
      'user_src': 'CORP\\menons4',
      'username': ''
    },
    {
      'action': 'openProcess',
      'agent_id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'alias_host': 'WIN7ENTX64',
      'analysis_file': '',
      'analysis_service': '',
      'analysis_session': '',
      'category': 'Process Event',
      'data': [
        {
          'filename': '',
          'hash': '',
          'size': 41
        }
      ],
      'description': '',
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
        'filename': 'cmd.exe',
        'hash': '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
        'launch_argument': 'cmd.exe  /C sc stop dtfsvc && sc delete dtfsvc',
        'path': 'C:\\WINDOWS\\System99\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': ''
        }
      },
      'detected_by': '-nwendpoint',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': 'nwendpoint'
      },
      'device_type': 'nwendpoint',
      'domain': 'INENMENONS4L2C',
      'domain_dst': '',
      'domain_src': '',
      'enrichment': '',
      'event_source': '10.63.0.117:56005',
      'event_source_id': '857783',
      'file': '',
      'from': '',
      'host_dst': '',
      'host_src': '',
      'hostname': 'INENMENONS4L2C',
      'operating_system': 'windows',
      'port_dst': '',
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857783'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
        }
      ],
      'size': 41,
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
          'ip_address': '',
          'mac_address': '',
          'netbios_name': '',
          'port': ''
        },
        'filename': 'dtf.exe',
        'hash': '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
        'launch_argument': 'dtf.exe  -dll:ioc.dll -testcase:353',
        'path': 'C:\\WINDOWS\\System21\\',
        'user': {
          'ad_domain': '',
          'ad_username': '',
          'email_address': '',
          'username': 'CORP\\menons4'
        }
      },
      'timestamp': 1528429212000,
      'to': '',
      'type': 'Endpoint',
      'user': 'CORP\\menons4',
      'user_account': '',
      'user_dst': '',
      'user_src': 'CORP\\menons4',
      'username': ''
    }
  ]
);
