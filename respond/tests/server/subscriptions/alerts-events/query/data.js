export default [].concat(
  [
    {
      'score': '3',
      'size': '2345',
      'detected_by': 'INENDEBS1L2C',
      'destination': {
        'device': {
          'dns_hostname': 'host.example.com',
          'dns_domain': 'example.com',
          'ip_address': '192.168.1.1'
        }
      },
      'description': 'IPIOC',
      'from': 'INENDEBS1L2C',
      'source': {
        'device': {
          'dns_hostname': 'INENDEBS1L2C',
          'os': 'Windows 7',
          'mac_address': '11-11-11-11-11-11-11-11',
          'ecat_agent_id': '26C5C21F-4DA8-3A00-437C-AB7444987430',
          'ip_address': '192.168.1.1'
        }
      },
      'to': 'host.example.com',
      'type': 'Instant IOC',
      'user': '',
      'timestamp': 1399530494000
    }
  ], [
    {
      'score': 257,
      'detected_by': 'INENDEBS1L2C',
      'description': 'MachineIOC',
      'source': {
        'user': {
          'username': 'example\\alice'
        }
      },
      'type': 'Instant IOC',
      'detector': {
        'dns_hostname': 'INENDEBS1L2C',
        'os': 'Windows 7',
        'mac_address': '11-11-11-11-11-11-11-11',
        'last_scanned': '05/02/2014 12:56:43',
        'dns_domain': 'example.com',
        'ecat_agent_id': '26C5C21F-4DA8-3A00-437C-AB7444987430',
        'ip_address': '192.168.1.1',
        'ldap_ou': 'CN=MACHINENAME,OU=Servers,DC=corp,DC=example,DC=com'
      },
      'user': 'example\\alice',
      'timestamp': 1399530494000
    }
  ], [
    {
      'score': '1-2-3-4',
      'file': 'filename.exe',
      'data': [
        {
          'yara_result': 'N YARA rules matched',
          'filename': 'filename.exe',
          'size': '23562',
          'opswat_result': 'OPSWAT result here',
          'bit9_status': 'bad',
          'module_signature': 'ABC Inc.',
          'hash': 'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3'
        }
      ],
      'size': '23562',
      'detected_by': 'INENDEBS1L2C',
      'description': 'ModuleIOC',
      'type': 'Instant IOC',
      'detector': {
        'dns_hostname': 'INENDEBS1L2C',
        'os': 'Windows 7',
        'mac_address': '11-11-11-11-11-11-11-11',
        'ecat_agent_id': '26C5C21F-4DA8-3A00-437C-AB7444987430',
        'ip_address': '192.168.1.1'
      },
      'user': '',
      'timestamp': 1399530494000
    }
  ], [
    {
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/event/AUTO/29589'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D"ad.adclick.co.kr"%2Fdate%2F2015-10-27T18%3A37%3A05.000Z%2F2015-10-27T18%3A47%3A05.000Z'
        }
      ],
      'data': [
        {
          'filename': '',
          'size': null,
          'hash': '3a90a7365a334c1481c8d3972aee40ad'
        }
      ],
      'destination': {
        'device': {
          'compliance_rating': '',
          'netbios_name': '',
          'port': '',
          'mac_address': '',
          'criticality': '',
          'asset_type': '',
          'ip_address': '3.3.3.3',
          'facility': '',
          'business_unit': '',
          'geolocation': {
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'description': '',
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
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'type': 'Unknown',
      'enrichment': {
        'command_control': {
          'weighted_domain_ua_ratio_score': 37,
          'weighted_domain_referer_ratio_score': 21,
          'weighted_domain_referer_score': 23,
          'confidence': 80,
          'weighted_whois_age_score': 12,
          'weighted_whois_validity_score': 12,
          'aggregate': 20
        },
        'whois': {
          'scaled_validity': 92.1,
          'domain_name': 'ad.adclick.co.kr',
          'estimated_domain_validity_days': 3000,
          'scaled_age': 90.5,
          'expires_date': '10-dec-2015',
          'estimated_domain_age_days': 2000,
          'is_cached': false,
          'created_date': '23-sep-2015',
          'updated_date': '2015-09-26',
          'source': 'DATABASE',
          'age_score': 10.3,
          'validity_score': 9.1
        },
        'context_hub': {
          'domain_is_whitelisted': false
        },
        'normalized': {
          'domain': 'ad.adclick.co.kr',
          'url': '',
          'timestamp': 1445971625844,
          'domain_sip': 'ad.adclick.co.kr2.2.2.2'
        },
        'domain': {
          'ua_ratio_score': 91.2,
          'referer_num_events': 106,
          'ua_cardinality': 10,
          'referer_ratio': 0.2,
          'referer_cardinality': 10,
          'referer_conditional_cardinality': 2,
          'ua_num_events': 106,
          'ua_score': 96.5,
          'referer_ratio_score': 91.2,
          'referer_score': 96.5,
          'ua_conditional_cardinality': 2,
          'ua_ratio': 0.2
        },
        'beaconing': {
          'beaconing_score': 99,
          'beaconing_period': 3622
        },
        'new_domain': {
          'age_age': 10000000,
          'age_score': 98.7
        },
        'user_agent': {
          'rare_ratio': 0.2,
          'rare_num_events': 106,
          'rare_conditional_cardinality': 2,
          'rare_ratio_score': 91.2,
          'rare_score': 96.5,
          'rare_cardinality': 10
        },
        'smooth': {
          'smooth_beaconing_score': 99.2
        }
      },
      'file': '',
      'size': null,
      'detected_by': '',
      'domain': 'ad.adclick.co.kr',
      'from': '',
      'to': '3.3.3.6',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': ''
      },
      'user': '',
      'timestamp': 1445971625844
    },
    {
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/event/AUTO/29589'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D"ad.adclick.co.kr"%2Fdate%2F2015-10-27T18%3A37%3A05.000Z%2F2015-10-27T18%3A47%3A05.000Z'
        }
      ],
      'data': [
        {
          'filename': '',
          'size': null,
          'hash': '3a90a7365a334c1481c8d3972aee40ad'
        }
      ],
      'destination': {
        'device': {
          'compliance_rating': '',
          'netbios_name': '',
          'port': '',
          'mac_address': '',
          'criticality': '',
          'asset_type': '',
          'ip_address': '3.3.3.3',
          'facility': '',
          'business_unit': '',
          'geolocation': {
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'description': '',
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
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'type': 'Unknown',
      'enrichment': {
        'command_control': {
          'weighted_domain_ua_ratio_score': 37,
          'weighted_domain_referer_ratio_score': 21,
          'weighted_domain_referer_score': 23,
          'confidence': 80,
          'weighted_whois_age_score': 12,
          'weighted_whois_validity_score': 12,
          'aggregate': 20
        },
        'whois': {
          'scaled_validity': 92.1,
          'domain_name': 'ad.adclick.co.kr',
          'estimated_domain_validity_days': 3000,
          'scaled_age': 90.5,
          'expires_date': '10-dec-2015',
          'estimated_domain_age_days': 2000,
          'is_cached': false,
          'created_date': '23-sep-2015',
          'updated_date': '2015-09-26',
          'source': 'DATABASE',
          'age_score': 10.3,
          'validity_score': 9.1
        },
        'context_hub': {
          'domain_is_whitelisted': false
        },
        'normalized': {
          'domain': 'ad.adclick.co.kr',
          'url': '',
          'timestamp': 1445971625844,
          'domain_sip': 'ad.adclick.co.kr2.2.2.2'
        },
        'domain': {
          'ua_ratio_score': 91.2,
          'referer_num_events': 106,
          'ua_cardinality': 10,
          'referer_ratio': 0.2,
          'referer_cardinality': 10,
          'referer_conditional_cardinality': 2,
          'ua_num_events': 106,
          'ua_score': 96.5,
          'referer_ratio_score': 91.2,
          'referer_score': 96.5,
          'ua_conditional_cardinality': 2,
          'ua_ratio': 0.2
        },
        'beaconing': {
          'beaconing_score': 99,
          'beaconing_period': 3622
        },
        'new_domain': {
          'age_age': 10000000,
          'age_score': 98.7
        },
        'user_agent': {
          'rare_ratio': 0.2,
          'rare_num_events': 106,
          'rare_conditional_cardinality': 2,
          'rare_ratio_score': 91.2,
          'rare_score': 96.5,
          'rare_cardinality': 10
        },
        'smooth': {
          'smooth_beaconing_score': 99.2
        }
      },
      'file': '',
      'size': null,
      'detected_by': '',
      'domain': 'ad.adclick.co.kr',
      'from': '',
      'to': '3.3.3.7',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': ''
      },
      'user': '',
      'timestamp': 1445971625844
    },
    {
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/event/AUTO/29589'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D"ad.adclick.co.kr"%2Fdate%2F2015-10-27T18%3A37%3A05.000Z%2F2015-10-27T18%3A47%3A05.000Z'
        }
      ],
      'data': [
        {
          'filename': '',
          'size': null,
          'hash': '3a90a7365a334c1481c8d3972aee40ad'
        }
      ],
      'destination': {
        'device': {
          'compliance_rating': '',
          'netbios_name': '',
          'port': '',
          'mac_address': '',
          'criticality': '',
          'asset_type': '',
          'ip_address': '3.3.3.3',
          'facility': '',
          'business_unit': '',
          'geolocation': {
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'description': '',
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
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'type': 'Unknown',
      'enrichment': {
        'command_control': {
          'weighted_domain_ua_ratio_score': 37,
          'weighted_domain_referer_ratio_score': 21,
          'weighted_domain_referer_score': 23,
          'confidence': 80,
          'weighted_whois_age_score': 12,
          'weighted_whois_validity_score': 12,
          'aggregate': 20
        },
        'whois': {
          'scaled_validity': 92.1,
          'domain_name': 'ad.adclick.co.kr',
          'estimated_domain_validity_days': 3000,
          'scaled_age': 90.5,
          'expires_date': '10-dec-2015',
          'estimated_domain_age_days': 2000,
          'is_cached': false,
          'created_date': '23-sep-2015',
          'updated_date': '2015-09-26',
          'source': 'DATABASE',
          'age_score': 10.3,
          'validity_score': 9.1
        },
        'context_hub': {
          'domain_is_whitelisted': false
        },
        'normalized': {
          'domain': 'ad.adclick.co.kr',
          'url': '',
          'timestamp': 1445971625844,
          'domain_sip': 'ad.adclick.co.kr2.2.2.2'
        },
        'domain': {
          'ua_ratio_score': 91.2,
          'referer_num_events': 106,
          'ua_cardinality': 10,
          'referer_ratio': 0.2,
          'referer_cardinality': 10,
          'referer_conditional_cardinality': 2,
          'ua_num_events': 106,
          'ua_score': 96.5,
          'referer_ratio_score': 91.2,
          'referer_score': 96.5,
          'ua_conditional_cardinality': 2,
          'ua_ratio': 0.2
        },
        'beaconing': {
          'beaconing_score': 99,
          'beaconing_period': 3622
        },
        'new_domain': {
          'age_age': 10000000,
          'age_score': 98.7
        },
        'user_agent': {
          'rare_ratio': 0.2,
          'rare_num_events': 106,
          'rare_conditional_cardinality': 2,
          'rare_ratio_score': 91.2,
          'rare_score': 96.5,
          'rare_cardinality': 10
        },
        'smooth': {
          'smooth_beaconing_score': 99.2
        }
      },
      'file': '',
      'size': null,
      'detected_by': '',
      'domain': 'ad.adclick.co.kr',
      'from': '',
      'to': '3.3.3.8',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': ''
      },
      'user': '',
      'timestamp': 1445971625844
    },
    {
      'related_links': [
        {
          'type': 'investigate_original_event',
          'url': '/investigation/host/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/event/AUTO/29589'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D"ad.adclick.co.kr"%2Fdate%2F2015-10-27T18%3A37%3A05.000Z%2F2015-10-27T18%3A47%3A05.000Z'
        }
      ],
      'data': [
        {
          'filename': '',
          'size': null,
          'hash': '3a90a7365a334c1481c8d3972aee40ad'
        }
      ],
      'destination': {
        'device': {
          'compliance_rating': '',
          'netbios_name': '',
          'port': '',
          'mac_address': '',
          'criticality': '',
          'asset_type': '',
          'ip_address': '3.3.3.3',
          'facility': '',
          'business_unit': '',
          'geolocation': {
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'description': '',
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
            'country': '',
            'city': '',
            'latitude': null,
            'organization': '',
            'domain': '',
            'longitude': null
          }
        },
        'user': {
          'email_address': '',
          'ad_username': '',
          'ad_domain': '',
          'username': ''
        }
      },
      'type': 'Unknown',
      'enrichment': {
        'command_control': {
          'weighted_domain_ua_ratio_score': 37,
          'weighted_domain_referer_ratio_score': 21,
          'weighted_domain_referer_score': 23,
          'confidence': 80,
          'weighted_whois_age_score': 12,
          'weighted_whois_validity_score': 12,
          'aggregate': 20
        },
        'whois': {
          'scaled_validity': 92.1,
          'domain_name': 'ad.adclick.co.kr',
          'estimated_domain_validity_days': 3000,
          'scaled_age': 90.5,
          'expires_date': '10-dec-2015',
          'estimated_domain_age_days': 2000,
          'is_cached': false,
          'created_date': '23-sep-2015',
          'updated_date': '2015-09-26',
          'source': 'DATABASE',
          'age_score': 10.3,
          'validity_score': 9.1
        },
        'context_hub': {
          'domain_is_whitelisted': false
        },
        'normalized': {
          'domain': 'ad.adclick.co.kr',
          'url': '',
          'timestamp': 1445971625844,
          'domain_sip': 'ad.adclick.co.kr2.2.2.2'
        },
        'domain': {
          'ua_ratio_score': 91.2,
          'referer_num_events': 106,
          'ua_cardinality': 10,
          'referer_ratio': 0.2,
          'referer_cardinality': 10,
          'referer_conditional_cardinality': 2,
          'ua_num_events': 106,
          'ua_score': 96.5,
          'referer_ratio_score': 91.2,
          'referer_score': 96.5,
          'ua_conditional_cardinality': 2,
          'ua_ratio': 0.2
        },
        'beaconing': {
          'beaconing_score': 99,
          'beaconing_period': 3622
        },
        'new_domain': {
          'age_age': 10000000,
          'age_score': 98.7
        },
        'user_agent': {
          'rare_ratio': 0.2,
          'rare_num_events': 106,
          'rare_conditional_cardinality': 2,
          'rare_ratio_score': 91.2,
          'rare_score': 96.5,
          'rare_cardinality': 10
        },
        'smooth': {
          'smooth_beaconing_score': 99.2
        }
      },
      'file': '',
      'size': null,
      'detected_by': '',
      'domain': 'ad.adclick.co.kr',
      'from': '',
      'to': '3.3.3.9',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': ''
      },
      'user': '',
      'timestamp': 1445971625844
    }
  ]);