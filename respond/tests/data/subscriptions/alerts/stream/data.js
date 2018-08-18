export default [
  {
    'id': '586ecf95ecd25950034e1312',
    'receivedTime': 1483657109643,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': {
      'severity': 9,
      'device_version': '11.0.0000',
      'device_product': 'Event Stream Analysis',
      'signature_id': 'Suspected C&C',
      'model_name': 'C2-Log',
      'name': 'P2P software as detected by an Intrusion detection device',
      'device_vendor': 'RSA',
      'version': '1',
      'timestamp': 1483610607482
    },
    'originalRawAlert': null,
    'originalAlert': null,
    'incidentId': 'INC-18',
    'partOfIncident': true,
    'incidentCreated': 1483657112176,
    'timestamp': 1483610607482,
    'alert': {
      'severity': 90,
      'groupby_type': '',
      'related_links': [
        {
          'type': 'investigate_session',
          'url': '/investigation/10.101.217.47:50005/navigate/query/'
        },
        {
          'type': 'investigate_device_ip',
          'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D%2Fdate%2F2017-01-05T22%3A48%3A29.642Z%2F2017-01-05T23%3A08%3A29.642Z'
        },
        {
          'type': 'investigate_src_ip',
          'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2017-01-05T22%3A48%3A29.642Z%2F2017-01-05T23%3A08%3A29.642Z'
        },
        {
          'type': 'investigate_dst_ip',
          'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2017-01-05T22%3A48%3A29.642Z%2F2017-01-05T23%3A08%3A29.642Z'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%22g00gle.com%22%2Fdate%2F2017-01-05T22%3A48%3A29.642Z%2F2017-01-05T23%3A08%3A29.642Z'
        }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 90,
      'groupby_domain': 'g00gle.com',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'signature_id': 'Suspected C&C',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '3.3.3.3',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '10.64.188.48',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'type': ['Network'],
      'events': [
        {
          'referer': 'curl/7.24.0',
          'alias_host': 'g00gle.com',
          'data': [
            {
              'filename': '',
              'size': 0,
              'hash': ''
            }
          ],
          'destination': {
            'device': {
              'geolocation': {}
            },
            'user': {}
          },
          'source': {
            'device': {
              'geolocation': {}
            },
            'user': {}
          },
          'type': 'Unknown',
          'directory': '',
          'content': 'txt',
          'enrichment': {
            'ctxhub': {
              'domain_is_whitelisted': false
            },
            'normalized': {
              'full_domain': 'www.ncac.gwu.edu',
              'srcip_full_domain': '140.116.71.91_www.ncac.gwu.edu',
              'domain': 'gwu.edu',
              'user_agent': 'Mozilla/4.0',
              'timestamp': 1378239416000
            },
            'domain': {
              'ua_ratio_score': 100,
              'referer_num_events': 8,
              'ua_cardinality': 1,
              'referer_ratio': 0,
              'referer_cardinality': 1,
              'referer_conditional_cardinality': 0,
              'ua_num_events': 8,
              'ua_score': 100,
              'referer_ratio_score': 100,
              'referer_score': 100,
              'ua_conditional_cardinality': 1,
              'ua_ratio': 100
            },
            'whois': {
              'age_score': 100,
              'estimated_domain_age_days': 1,
              'validity_score': 100,
              'estimated_domain_validity_days': 1
            },
            'beaconing': {
              'beaconing_score': 0,
              'beaconing_period': 0
            },
            'new_domain': {
              'age_num_events': 14,
              'age_age': 0,
              'age_score': 99
            },
            'httpEventEnrichedRule': {
              'flow_name': 'C2'
            },
            'user_agent': {
              'rare_num_events': 14,
              'rare_score': 100,
              'rare_cardinality': 1
            },
            'smooth': {
              'smooth_beaconing_score': 100
            }
          },
          'file': '',
          'detected_by': '-,',
          'client': 'Microsoft IE 10.0',
          'action': 'GET',
          'from': '',
          'timestamp': 'Thu Jan 01 00:00:00 UTC 1970',
          'event_source_id': 'concentrator',
          'related_links': [
            {
              'type': 'investigate_original_event',
              'url': ''
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%27g00gle.com%27%2Fdate%2F2017-01-05T22%3A48%3A29.642Z%2F2017-01-05T23%3A08%3A29.642Z'
            }
          ],
          'ip_source': '10.64.188.48',
          'ip_dst': '3.3.3.3',
          'size': 0,
          'service': 80,
          'domain': 'g00gle.com',
          'to': '',
          'detector': {
            'device_class': '',
            'ip_address': '',
            'product_name': ''
          },
          'user': ''
        }
      ],
      'timestamp': 1483610607482
    }
  },
  {
    'id': '586ecfc0ecd25950034e1314',
    'receivedTime': 1483657152517,
    'status': 'NORMALIZED',
    'errorMessage': null,
    'originalHeaders': {
      'severity': 8,
      'device_version': '11.0.FIXME',
      'device_product': 'Event Stream Analysis',
      'signature_id': 'Suspected UBA VPN',
      'model_name': 'UbaCisco',
      'name': 'Suspected UBA VPN',
      'device_vendor': 'RSA',
      'version': 0,
      'timestamp': 1483657152000
    },
    'originalRawAlert': null,
    'originalAlert': null,
    'incidentId': null,
    'partOfIncident': false,
    'incidentCreated': null,
    'timestamp': 1483657152000,
    'alert': {
      'severity': 80,
      'groupby_type': '',
      'related_links': [
        {
          'type': 'investigate_session',
          'url': '/investigation/10.101.217.47:50005/navigate/query/sessionid%3D198775'
        },
        {
          'type': 'investigate_device_ip',
          'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D127.0.0.1%2Fdate%2F2017-01-05T22%3A49%3A12.515Z%2F2017-01-05T23%3A09%3A12.515Z'
        },
        {
          'type': 'investigate_src_ip',
          'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2017-01-05T22%3A49%3A12.515Z%2F2017-01-05T23%3A09%3A12.515Z'
        },
        {
          'type': 'investigate_dst_ip',
          'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2017-01-05T22%3A49%3A12.515Z%2F2017-01-05T23%3A09%3A12.515Z'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%22g00gle.com%22%2Fdate%2F2017-01-05T22%3A49%3A12.515Z%2F2017-01-05T23%3A09%3A12.515Z'
        }
      ],
      'host_summary': 'Firewall-,',
      'user_summary': [
        'Jake'
      ],
      'risk_score': 80,
      'groupby_domain': 'g00gle.com',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'signature_id': 'Suspected UBA VPN',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'Suspected UBA VPN',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '127.0.0.1',
      'type': ['Network'],
      'events': [
        {
          'event_byte_size': 119,
          'header_id': '0001',
          'alias_host': 'g00gle.com',
          'data': [
            {
              'filename': '',
              'size': 147,
              'hash': ''
            }
          ],
          'event_cat_name': 'Network.Connections',
          'destination': {
            'device': {
              'geolocation': {

              }
            },
            'user': {
              'username': 'Jake'
            }
          },
          'description': 'Network.Connections',
          'device_type': 'ciscoasa',
          'sessionid': 198775,
          'medium': 32,
          'source': {
            'device': {
              'geolocation': {

              }
            },
            'user': {

            }
          },
          'rid': 63681,
          'type': 'Network',
          'enrichment': {
            'rsa_analytics_uba-cisco_vpn_normalized_timestamp': 1483657088000000,
            'rsa_analytics_uba-cisco_vpn_rarehostscore_score': 25.91817793182821,
            'rsa_analytics_uba-cisco_vpn_aggregation_aggregate': 30.7183425465205,
            'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 45.11883639059736,
            'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 5.183635586365643,
            'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 25.91817793182821,
            'rsa_analytics_uba-cisco_vpn_newispscore_score': 45.11883639059736,
            'rsa_analytics_uba-cisco_vpn_rarehost_total_access': 1,
            'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 5.183635586365643,
            'rsa_analytics_uba-cisco_vpn_rarehost_cardinality': 1,
            'rsa_analytics_uba-cisco_vpn_newisp_cardinality': 2,
            'rsa_analytics_uba-cisco_vpn_rarehost_average_cardinality': 1,
            'rsa_analytics_uba-cisco_vpn_newispscore_ratio': 2,
            'rsa_analytics_uba-cisco_vpn_newisp_average_cardinality': 1,
            'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 5.183635586365643,
            'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 9.023767278119472,
            'rsa_analytics_uba-cisco_vpn_rarehost_element_access_count': 1,
            'rsa_analytics_uba-cisco_vpn_rarehostscore_ratio': 1,
            'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 25.91817793182821,
            'rsa_analytics_uba-cisco_vpn_aggregation_confidence': 80,
            'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 25.91817793182821
          },
          'file': '',
          'detected_by': 'Firewall-,',
          'from': '',
          'msg_id': '734003:01',
          'timestamp': 'Thu Jan 05 22:58:08 UTC 2017',
          'process': 'DAP',
          'event_source_id': 'concentrator',
          'related_links': [
            {
              'type': 'investigate_original_event',
              'url': ''
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%27g00gle.com%27%2Fdate%2F2017-01-05T22%3A49%3A12.515Z%2F2017-01-05T23%3A09%3A12.515Z'
            }
          ],
          'level': 7,
          'device_ip': '127.0.0.1',
          'user_dst': 'Jake',
          'size': 147,
          'domain': 'g00gle.com',
          'device_class': 'Firewall',
          'time': 1483657088000,
          'to': '',
          'ip_addr': '192.64.188.48',
          'detector': {
            'device_class': 'Firewall',
            'ip_address': '127.0.0.1',
            'product_name': 'ciscoasa'
          },
          'user': 'Jake',
          'did': 'logd11-114-42'
        }
      ],
      'timestamp': 1483657152000
    }
  },
  {
    'id': '586ecfc0ecd25950034e1318',
    'errorMessage': null,
    'incidentCreated': null,
    'incidentId': null,
    'originalAlert': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'partOfIncident': false,
    'receivedTime': 1532658252079,
    'status': 'NORMALIZED',
    'timestamp': 1532297340000,
    'alert': {
      'id': '1486f9ac-974d-4be6-8641-1b0826097854',
      'entity_id': '1c86c083-d82d-47f4-8930-187473ddad13',
      'classification': 'abnormal_ad_changes',
      'classifier_id': '3af8801b-0979-4066-b906-6330eaca2337',
      'classifier_score': 97.41587325223246,
      'classifier_severity': 'HIGH',
      'end_date': '2018-07-22T22:09:00.000+0000',
      'entity_name': 'ad_qa_1_9',
      'groupby_analysis_file': '',
      'groupby_analysis_service': '',
      'groupby_analysis_session': '',
      'groupby_c2domain': '',
      'groupby_data_hash': '',
      'groupby_destination_country': '',
      'groupby_destination_ip': '',
      'groupby_destination_port': '',
      'groupby_detector_ip': '',
      'groupby_device_type': '',
      'groupby_domain': '',
      'groupby_domain_dst': '',
      'groupby_domain_src': '',
      'groupby_filename': '',
      'groupby_host_dst': '',
      'groupby_host_src': '',
      'groupby_source_country': '',
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_type': 'User Entity',
      'groupby_user_dst': '',
      'groupby_user_src': '',
      'groupby_username': '',
      'name': 'abnormal_object_change_operation',
      'numEvents': 2,
      'risk_score': 4.0,
      'severity': 4,
      'signature_id': 'UEBAIOC',
      'source': 'User Entity Behavior Analytics',
      'start_date': '2018-07-22T22:09:00.000+0000',
      'timestamp': 1532297340000,
      'type': ['User Entity'],
      'events': [
        {
          'createdDate': '2018-07-27T02:23:28.229+0000',
          'eventTime': '2018-07-22T22:09:00.000+0000',
          'features': {
            'additionalInfo': {},
            'dataSource': '4741',
            'eventDate': {
              'epochSecond': 1532297340,
              'nano': 0
            },
            'eventId': 'EV-828-ad_qa_1_9',
            'id': '5b5a81388402c7548eacfacd',
            'isUserAdmin': false,
            'objectId': 'Test5',
            'operationType': 'COMPUTER_ACCOUNT_CREATED',
            'operationTypeCategories': [
              'COMPUTER_MANAGEMENT',
              'OBJECT_MANAGEMENT'
            ],
            'result': 'SUCCESS',
            'userDisplayName': 'ad_qa_1_9',
            'userId': 'ad_qa_1_9',
            'userName': 'ad_qa_1_9'
          },
          'id': 'c05972b1-db65-4786-bf86-d4f1541aa161',
          'indicatorId': '1486f9ac-974d-4be6-8641-1b0826097854',
          'schema': 'ACTIVE_DIRECTORY',
          'scores': {
            'operationType': 4.0
          },
          'updatedBy': 'hourlyOutputProcessorRun2018-07-22T22:00:00Z',
          'updatedDate': '2018-07-27T02:23:28.229+0000'
        },
        {
          'createdDate': '2018-07-27T02:23:28.229+0000',
          'eventTime': '2018-07-22T22:42:00.000+0000',
          'features': {
            'additionalInfo': {},
            'dataSource': '4741',
            'eventDate': {
              'epochSecond': 1532299320,
              'nano': 0
            },
            'eventId': 'EV-861-ad_qa_1_9',
            'id': '5b5a81388402c7548eacfaf0',
            'isUserAdmin': false,
            'objectId': 'Test3',
            'operationType': 'COMPUTER_ACCOUNT_CREATED',
            'operationTypeCategories': [
              'COMPUTER_MANAGEMENT',
              'OBJECT_MANAGEMENT'
            ],
            'result': 'SUCCESS',
            'userDisplayName': 'ad_qa_1_9',
            'userId': 'ad_qa_1_9',
            'userName': 'ad_qa_1_9'
          },
          'id': '44732bcc-b9e8-4b2d-badb-e8747c98db46',
          'indicatorId': '1486f9ac-974d-4be6-8641-1b0826097854',
          'schema': 'ACTIVE_DIRECTORY',
          'scores': {
            'operationType': 4.0
          },
          'updatedBy': 'hourlyOutputProcessorRun2018-07-22T22:00:00Z',
          'updatedDate': '2018-07-27T02:23:28.229+0000'
        }
      ]
    }
  }
];
