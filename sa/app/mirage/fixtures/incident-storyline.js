export default {
  'relatedIndicators': [
    {
      'indicator': {
        'id': '58586f34d848c66ea0375f7b',
        'receivedTime': 1482190644131,
        'status': 'GROUPED_IN_INCIDENT',
        'errorMessage': null,
        'originalHeaders': {
          'severity': 5,
          'device_version': '11.0.0000',
          'device_product': 'Event Stream Analysis',
          'signature_id': 'Suspected C&C',
          'model_name': 'C2-Log',
          'name': 'P2P software as detected by an Intrusion detection device',
          'device_vendor': 'RSA',
          'version': '1',
          'timestamp': 1481882607482
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': 'INC-9',
        'partOfIncident': true,
        'incidentCreated': 1482190647082,
        'timestamp': 1481882607482,
        'alert': {
          'severity': 50,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-12-19T23%3A27%3A24.130Z%2F2016-12-19T23%3A47%3A24.130Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-12-19T23%3A27%3A24.130Z%2F2016-12-19T23%3A47%3A24.130Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-12-19T23%3A27%3A24.130Z%2F2016-12-19T23%3A47%3A24.130Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%22g00gle.com%22%2Fdate%2F2016-12-19T23%3A27%3A24.130Z%2F2016-12-19T23%3A47%3A24.130Z'
            }
          ],
          'host_summary': '-,',
          'user_summary': [],
          'risk_score': 50,
          'groupby_domain': 'g00gle.com',
          'source': 'Event Stream Analysis',
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              '',
              '',
              'g00gle.com',
              '76.64.188.48',
              '3.3.3.3',
              ''
            ]
          ],
          'signature_id': 'Suspected C&C',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '3.3.3.3',
          'name': 'P2P software as detected by an Intrusion detection device',
          'numEvents': 1,
          'groupby_source_ip': '76.64.188.48',
          'groupby_source_username': '',
          'groupby_detector_ip': '',
          'events': [{
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
              'rsa_analytics_http-log_c2_command_control_weighted_rsa_analytics_http-log_c2_referer_ratio_score': 22,
              'rsa_analytics_http-log_c2_normalized_timestamp': 1457714844000,
              'rsa_analytics_http-log_c2_whois_estimated_domain_age_days': 182,
              'rsa_analytics_http-log_c2_whois_estimated_domain_validity_days': 182,
              'rsa_analytics_http-log_c2_referer_ratio_score': 100,
              'rsa_analytics_http-log_c2_newdomain_age': 0,
              'rsa_analytics_http-log_c2_command_control_aggregate': 90.7626911163496,
              'rsa_analytics_http-log_c2_normalized_user_agent': 'Mozilla/5.0 (Linux) Gecko Iceweasel (Debian) Mnenhy',
              'rsa_analytics_http-log_c2_ua_ratio_score': 100,
              'rsa_analytics_http-log_c2_ua_ratio': 100,
              'rsa_analytics_http-log_c2_referer_ratio': 100,
              'rsa_analytics_http-log_c2_normalized_srcip_full_domain': '76.64.188.48_g00gle.com',
              'rsa_analytics_http-log_c2_ua_cond_cardinality': 2,
              'rsa_analytics_http-log_c2_newdomain_score': 100,
              'rsa_analytics_http-log_c2_whois_validity_score': 83.5030897216977,
              'rsa_analytics_http-log_c2_useragent_score': 95.1229424500714,
              'rsa_analytics_http-log_c2_newdomain_num_events': 287,
              'rsa_analytics_http-log_c2_referer_num_events': 282,
              'rsa_analytics_http-log_c2_useragent_cardinality': 2,
              'rsa_analytics_http-log_c2_ua_score': 81.8730753077982,
              'rsa_analytics_http-log_c2_smooth_score': 98.1475732083557,
              'rsa_analytics_http-log_c2_referer_cardinality': 2,
              'rsa_analytics_http-log_c2_whois_domain_not_found_by_whois': true,
              'rsa_analytics_http-log_c2_referer_cond_cardinality': 2,
              'rsa_analytics_http-log_c2_whois_scaled_age': 10,
              'rsa_analytics_http-log_c2_command_control_confidence': 69,
              'rsa_analytics_http-log_c2_command_control_weighted_rsa_analytics_http-log_c2_whois_age_score': 38.1211641786303,
              'rsa_analytics_http-log_c2_beaconing_score': 24.6912220309873,
              'rsa_analytics_http-log_c2_normalized_full_domain': 'g00gle.com',
              'rsa_analytics_http-log_c2_command_control_weighted_rsa_analytics_http-log_c2_whois_validity_score': 2.50509269165093,
              'rsa_analytics_http-log_c2_whois_age_score': 86.639009496887,
              'rsa_analytics_http-log_c2_beaconing_period': 26600,
              'rsa_analytics_http-log_c2_referer_score': 80.2518797962478,
              'rsa_analytics_http-log_c2_ua_num_events': 282,
              'rsa_analytics_http-log_c2_useragent_num_events': 288,
              'rsa_analytics_http-log_c2_whois_scaled_validity': 10,
              'rsa_analytics_http-log_c2_ua_cardinality': 2,
              'rsa_analytics_http-log_c2_normalized_domain': 'g00gle.com'
            },
            'file': '',
            'detected_by': '-,',
            'client': 'Microsoft IE 10.0',
            'action': 'GET',
            'from': '',
            'timestamp': 'Thu Jan 01 00:00:00 UTC 1970',
            'event_source_id': 'DEV1-IM-Concentrator.grcrtp.local:50005:2958900',
            'related_links': [
              {
                'type': 'investigate_original_event',
                'url': ''
              },
              {
                'type': 'investigate_destination_domain',
                'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%27g00gle.com%27%2Fdate%2F2016-12-19T23%3A27%3A24.130Z%2F2016-12-19T23%3A47%3A24.130Z'
              }
            ],
            'ip_source': '76.64.188.48',
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
          }],
          'timestamp': 1481882607482
        }
      },
      'level': 3,
      'matched': [
        '',
        '',
        'g00gle.com',
        '76.64.188.48',
        '',
        ''
      ],
      'group': '0',
      'lookup': {}
    },
    {
      'indicator': {
        'id': '58586f59d848c66ea0375f7c',
        'receivedTime': 1482190681304,
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
          'timestamp': 1482190681000
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': null,
        'partOfIncident': false,
        'incidentCreated': null,
        'timestamp': 1482190681000,
        'alert': {
          'severity': 80,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/10.101.217.47:50005/navigate/query/sessionid%3D152141'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D127.0.0.1%2Fdate%2F2016-12-19T23%3A28%3A01.302Z%2F2016-12-19T23%3A48%3A01.302Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2016-12-19T23%3A28%3A01.302Z%2F2016-12-19T23%3A48%3A01.302Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-12-19T23%3A28%3A01.302Z%2F2016-12-19T23%3A48%3A01.302Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%22rsa.com%22%2Fdate%2F2016-12-19T23%3A28%3A01.302Z%2F2016-12-19T23%3A48%3A01.302Z'
            }
          ],
          'host_summary': 'Firewall-,',
          'user_summary': [
            'Jake'
          ],
          'risk_score': 80,
          'groupby_domain': 'rsa.com',
          'source': 'Event Stream Analysis',
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              'Jake',
              '',
              'rsa.com',
              '',
              '',
              ''
            ]
          ],
          'signature_id': 'Suspected UBA VPN',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'Suspected UBA VPN',
          'numEvents': 1,
          'groupby_source_ip': '',
          'groupby_source_username': '',
          'groupby_detector_ip': '127.0.0.1',
          'events': [
            {
              'event_byte_size': 116,
              'header_id': '0001',
              'alias_host': 'rsa.com',
              'data': [
                {
                  'filename': '',
                  'size': 144,
                  'hash': ''
                }
              ],
              'event_cat_name': 'Network.Connections',
              'destination': {
                'device': {
                  'geolocation': {}
                },
                'user': {
                  'username': 'Jake'
                }
              },
              'description': 'Network.Connections',
              'device_type': 'ciscoasa',
              'sessionid': 152141,
              'medium': 32,
              'source': {
                'device': {
                  'geolocation': {}
                },
                'user': {}
              },
              'rid': 17094,
              'type': 'Network',
              'enrichment': {
                'rsa_analytics_uba-cisco_vpn_normalized_timestamp': 1482190629000000,
                'rsa_analytics_uba-cisco_vpn_rarehostscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_aggregation_aggregate': 23.188264930349032,
                'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 31.102120748114224,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 3.57327610414537,
                'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 17.86638052072685,
                'rsa_analytics_uba-cisco_vpn_newispscore_score': 0,
                'rsa_analytics_uba-cisco_vpn_rarehost_total_access': 1,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 3.57327610414537,
                'rsa_analytics_uba-cisco_vpn_rarehost_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_newisp_cardinality': 0,
                'rsa_analytics_uba-cisco_vpn_rarehost_average_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_newispscore_ratio': 0,
                'rsa_analytics_uba-cisco_vpn_newisp_average_cardinality': 0.9575256423365532,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 6.220424149622845,
                'rsa_analytics_uba-cisco_vpn_rarehost_element_access_count': 1,
                'rsa_analytics_uba-cisco_vpn_rarehostscore_ratio': 1,
                'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 17.86638052072685,
                'rsa_analytics_uba-cisco_vpn_aggregation_confidence': 80,
                'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 25.91817793182821
              },
              'file': '',
              'detected_by': 'Firewall-,',
              'from': '',
              'msg_id': '734003:01',
              'timestamp': 'Mon Dec 19 23:37:09 UTC 2016',
              'process': 'DAP',
              'event_source_id': '10.101.217.47:50005:152141',
              'related_links': [
                {
                  'type': 'investigate_original_event',
                  'url': ''
                },
                {
                  'type': 'investigate_destination_domain',
                  'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%27rsa.com%27%2Fdate%2F2016-12-19T23%3A28%3A01.302Z%2F2016-12-19T23%3A48%3A01.302Z'
                }
              ],
              'level': 7,
              'device_ip': '127.0.0.1',
              'user_dst': 'Jake',
              'size': 144,
              'domain': 'rsa.com',
              'device_class': 'Firewall',
              'time': 1482190629000,
              'to': '',
              'ip_addr': '24.63.161.200',
              'detector': {
                'device_class': 'Firewall',
                'ip_address': '127.0.0.1',
                'product_name': 'ciscoasa'
              },
              'user': 'Jake',
              'did': 'logd11-114-42'
            }
          ],
          'timestamp': 1482190681000
        }
      },
      'level': 1,
      'matched': [
        'jake',
        '',
        '',
        '',
        '',
        ''
      ],
      'group': '',
      'lookup': {
        'jake': null
      }
    },
    {
      'indicator': {
        'id': '58586f59d848c66ea0375f7d',
        'receivedTime': 1482190681309,
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
          'timestamp': 1482190681000
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': null,
        'partOfIncident': false,
        'incidentCreated': null,
        'timestamp': 1482190681000,
        'alert': {
          'severity': 80,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/10.101.217.47:50005/navigate/query/sessionid%3D152140'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D127.0.0.1%2Fdate%2F2016-12-19T23%3A28%3A01.308Z%2F2016-12-19T23%3A48%3A01.308Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2016-12-19T23%3A28%3A01.308Z%2F2016-12-19T23%3A48%3A01.308Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-12-19T23%3A28%3A01.308Z%2F2016-12-19T23%3A48%3A01.308Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%2210.64.134.74%22%2Fdate%2F2016-12-19T23%3A28%3A01.308Z%2F2016-12-19T23%3A48%3A01.308Z'
            }
          ],
          'host_summary': 'Firewall-,',
          'user_summary': [
            'Jake'
          ],
          'risk_score': 80,
          'groupby_domain': '10.64.134.74',
          'source': 'Event Stream Analysis',
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              'Jake',
              '',
              '10.64.134.74',
              '',
              '',
              ''
            ]
          ],
          'signature_id': 'Suspected UBA VPN',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'Suspected UBA VPN',
          'numEvents': 1,
          'groupby_source_ip': '',
          'groupby_source_username': '',
          'groupby_detector_ip': '127.0.0.1',
          'events': [
            {
              'event_byte_size': 167,
              'header_id': '0001',
              'alias_host': '10.64.134.74',
              'data': [
                {
                  'filename': '',
                  'size': 159,
                  'hash': ''
                }
              ],
              'event_cat_name': 'Network.Connections.Successful',
              'destination': {
                'device': {
                  'geolocation': {}
                },
                'user': {
                  'username': 'Jake'
                }
              },
              'description': 'assigned to session',
              'device_type': 'ciscoasa',
              'sessionid': 152140,
              'medium': 32,
              'source': {
                'device': {
                  'geolocation': {}
                },
                'user': {}
              },
              'rid': 17093,
              'type': 'Network',
              'enrichment': {
                'rsa_analytics_uba-cisco_vpn_normalized_timestamp': 1482190629000000,
                'rsa_analytics_uba-cisco_vpn_aggregation_aggregate': 27.21416363589972,
                'rsa_analytics_uba-cisco_vpn_geoip_organization': 'Bell Canada',
                'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 31.102120748114224,
                'rsa_analytics_uba-cisco_vpn_geoip_country_code': 'CA',
                'rsa_analytics_uba-cisco_vpn_rarelocation_total_access': 1,
                'rsa_analytics_uba-cisco_vpn_rareisp_element_access_count': 1,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_newispscore_score': 0,
                'rsa_analytics_uba-cisco_vpn_rarelocationscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_newisp_cardinality': 0,
                'rsa_analytics_uba-cisco_vpn_geoip_region': 'ON',
                'rsa_analytics_uba-cisco_vpn_newisp_average_cardinality': 0.9575256423365532,
                'rsa_analytics_uba-cisco_vpn_newispscore_ratio': 0,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 6.220424149622845,
                'rsa_analytics_uba-cisco_vpn_normalized_location': 'ON_CA',
                'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_aggregation_confidence': 80,
                'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_geoip_latitude': 44.893402,
                'rsa_analytics_uba-cisco_vpn_rarelocation_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_rareisp_average_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_geoip_country_name': 'Canada',
                'rsa_analytics_uba-cisco_vpn_rareispscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_geoip_postal_code': 'K0G',
                'rsa_analytics_uba-cisco_vpn_rareispscore_ratio': 1,
                'rsa_analytics_uba-cisco_vpn_rarelocationscore_ratio': 1,
                'rsa_analytics_uba-cisco_vpn_geoip_city': 'Kemptville',
                'rsa_analytics_uba-cisco_vpn_rareisp_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_rarelocation_average_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_geoip_longitude': -76.1836,
                'rsa_analytics_uba-cisco_vpn_rarelocation_element_access_count': 1,
                'rsa_analytics_uba-cisco_vpn_rareisp_total_access': 1
              },
              'file': '',
              'detected_by': 'Firewall-,',
              'from': '',
              'msg_id': '722051',
              'group': 'cihi_user_grp03_policy',
              'timestamp': 'Mon Dec 19 23:37:09 UTC 2016',
              'event_source_id': '10.101.217.47:50005:152140',
              'related_links': [
                {
                  'type': 'investigate_original_event',
                  'url': ''
                },
                {
                  'type': 'investigate_destination_domain',
                  'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%2710.64.134.74%27%2Fdate%2F2016-12-19T23%3A28%3A01.308Z%2F2016-12-19T23%3A48%3A01.308Z'
                }
              ],
              'level': 4,
              'device_ip': '127.0.0.1',
              'event_desc': 'assigned to session',
              'user_dst': 'Jake',
              'size': 159,
              'domain': '10.64.134.74',
              'device_class': 'Firewall',
              'time': 1482190629000,
              'to': '',
              'ip_addr': '76.64.188.48',
              'detector': {
                'device_class': 'Firewall',
                'ip_address': '127.0.0.1',
                'product_name': 'ciscoasa'
              },
              'user': 'Jake',
              'did': 'logd11-114-42'
            }
          ],
          'timestamp': 1482190681000
        }
      },
      'level': 1,
      'matched': [
        'jake',
        '',
        '',
        '',
        '',
        ''
      ],
      'group': '',
      'lookup': {
        'jake': null
      }
    },
    {
      'indicator': {
        'id': '58586f59d848c66ea0375f7e',
        'receivedTime': 1482190681314,
        'status': 'NORMALIZED',
        'errorMessage': null,
        'originalHeaders': {
          'severity': 8,
          'device_version': '11.0.FIXME',
          'device_product': 'Event Stream Analysis',
          'signature_id': 'Suspected UBA VPN',
          'name': 'P2P software as detected by an Intrusion detection device',
          'device_vendor': 'RSA',
          'version': 0,
          'timestamp': 1482190681000
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': null,
        'partOfIncident': false,
        'incidentCreated': null,
        'timestamp': 1482190681000,
        'alert': {
          'severity': 80,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/10.101.217.47:50005/navigate/query/sessionid%3D152145'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D127.0.0.1%2Fdate%2F2016-12-19T23%3A28%3A01.313Z%2F2016-12-19T23%3A48%3A01.313Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2016-12-19T23%3A28%3A01.313Z%2F2016-12-19T23%3A48%3A01.313Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-12-19T23%3A28%3A01.313Z%2F2016-12-19T23%3A48%3A01.313Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%22g00gle.com%22%2Fdate%2F2016-12-19T23%3A28%3A01.313Z%2F2016-12-19T23%3A48%3A01.313Z'
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
          'relationships': [
            [
              'Jake',
              '',
              'g00gle.com',
              '',
              '',
              ''
            ]
          ],
          'signature_id': 'Suspected UBA VPN',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'Suspected UBA VPN',
          'numEvents': 1,
          'groupby_source_ip': '',
          'groupby_source_username': '',
          'groupby_detector_ip': '127.0.0.1',
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
                  'geolocation': {}
                },
                'user': {
                  'username': 'Jake'
                }
              },
              'description': 'Network.Connections',
              'device_type': 'ciscoasa',
              'sessionid': 152145,
              'medium': 32,
              'source': {
                'device': {
                  'geolocation': {}
                },
                'user': {}
              },
              'rid': 17098,
              'type': 'Network',
              'enrichment': {
                'rsa_analytics_uba-cisco_vpn_normalized_timestamp': 1482190629000000,
                'rsa_analytics_uba-cisco_vpn_rarehostscore_score': 25.9181779318282,
                'rsa_analytics_uba-cisco_vpn_aggregation_aggregate': 27.2141636358997,
                'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 31.1021207481142,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 5.18363558636564,
                'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 25.9181779318282,
                'rsa_analytics_uba-cisco_vpn_newispscore_score': 0,
                'rsa_analytics_uba-cisco_vpn_rarehost_total_access': 2,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 5.18363558636564,
                'rsa_analytics_uba-cisco_vpn_rarehost_cardinality': 2,
                'rsa_analytics_uba-cisco_vpn_newisp_cardinality': 0,
                'rsa_analytics_uba-cisco_vpn_rarehost_average_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_newispscore_ratio': 0,
                'rsa_analytics_uba-cisco_vpn_newisp_average_cardinality': 0.957525642336553,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 5.18363558636564,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 6.22042414962285,
                'rsa_analytics_uba-cisco_vpn_rarehost_element_access_count': 1,
                'rsa_analytics_uba-cisco_vpn_rarehostscore_ratio': 1,
                'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 25.9181779318282,
                'rsa_analytics_uba-cisco_vpn_aggregation_confidence': 80,
                'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 25.9181779318282
              },
              'file': '',
              'detected_by': 'Firewall-,',
              'from': '',
              'msg_id': '734003:01',
              'timestamp': 'Mon Dec 19 23:37:09 UTC 2016',
              'process': 'DAP',
              'event_source_id': '10.101.217.47:50005:152145',
              'related_links': [
                {
                  'type': 'investigate_original_event',
                  'url': ''
                },
                {
                  'type': 'investigate_destination_domain',
                  'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%27g00gle.com%27%2Fdate%2F2016-12-19T23%3A28%3A01.313Z%2F2016-12-19T23%3A48%3A01.313Z'
                }
              ],
              'level': 7,
              'device_ip': '127.0.0.1',
              'user_dst': 'Jake',
              'size': 147,
              'domain': 'g00gle.com',
              'device_class': 'Firewall',
              'time': 1482190629000,
              'to': '',
              'ip_addr': '24.63.161.200',
              'detector': {
                'device_class': 'Firewall',
                'ip_address': '127.0.0.1',
                'product_name': 'ciscoasa'
              },
              'user': 'Jake',
              'did': 'logd11-114-42'
            }
          ],
          'timestamp': 1482190681000
        }
      },
      'level': 2,
      'matched': [
        'jake',
        '',
        'g00gle.com',
        '',
        '',
        ''
      ],
      'group': '',
      'lookup': {
        'jake': null
      }
    },
    {
      'indicator': {
        'id': '58586f59d848c66ea0375f7f',
        'receivedTime': 1482190681317,
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
          'timestamp': 1482190681000
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': null,
        'partOfIncident': false,
        'incidentCreated': null,
        'timestamp': 1482190681000,
        'alert': {
          'severity': 80,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/10.101.217.47:50005/navigate/query/sessionid%3D152152'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D127.0.0.1%2Fdate%2F2016-12-19T23%3A28%3A01.316Z%2F2016-12-19T23%3A48%3A01.316Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2016-12-19T23%3A28%3A01.316Z%2F2016-12-19T23%3A48%3A01.316Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-12-19T23%3A28%3A01.316Z%2F2016-12-19T23%3A48%3A01.316Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%22%22%2Fdate%2F2016-12-19T23%3A28%3A01.316Z%2F2016-12-19T23%3A48%3A01.316Z'
            }
          ],
          'host_summary': 'Firewall-,',
          'user_summary': [
            'Jake'
          ],
          'risk_score': 80,
          'groupby_domain': '',
          'source': 'Event Stream Analysis',
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              'Jake',
              '',
              '',
              '',
              '',
              ''
            ]
          ],
          'signature_id': 'Suspected UBA VPN',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'Suspected UBA VPN',
          'numEvents': 1,
          'groupby_source_ip': '',
          'groupby_source_username': '',
          'groupby_detector_ip': '127.0.0.1',
          'events': [
            {
              'event_byte_size': 176,
              'header_id': '0001',
              'data': [
                {
                  'filename': '',
                  'size': 239,
                  'hash': ''
                }
              ],
              'event_cat_name': 'Network.Connections.Terminations.VPN',
              'destination': {
                'device': {
                  'geolocation': {}
                },
                'user': {
                  'username': 'Jake'
                }
              },
              'description': 'Network.Connections.Terminations.VPN',
              'device_type': 'ciscoasa',
              'sessionid': 152152,
              'medium': 32,
              'source': {
                'device': {
                  'geolocation': {}
                },
                'user': {}
              },
              'rid': 17105,
              'type': 'Network',
              'enrichment': {
                'rsa_analytics_uba-cisco_vpn_normalized_timestamp': 1482190629000000,
                'rsa_analytics_uba-cisco_vpn_aggregation_aggregate': 27.21416363589972,
                'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 31.102120748114224,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_newispscore_score': 0,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_newisp_cardinality': 0,
                'rsa_analytics_uba-cisco_vpn_newispscore_ratio': 0,
                'rsa_analytics_uba-cisco_vpn_newisp_average_cardinality': 0.9575256423365532,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 6.220424149622845,
                'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_aggregation_confidence': 80,
                'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 25.91817793182821
              },
              'file': '',
              'detected_by': 'Firewall-,',
              'action': [
                'Session disconnected.'
              ],
              'from': '',
              'msg_id': '113019:01',
              'bytes_src': 1119848,
              'group': 'nahanvpn',
              'timestamp': 'Mon Dec 19 23:37:09 UTC 2016',
              'event_source_id': '10.101.217.47:50005:152152',
              'related_links': [
                {
                  'type': 'investigate_original_event',
                  'url': ''
                },
                {
                  'type': 'investigate_destination_domain',
                  'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%27%27%2Fdate%2F2016-12-19T23%3A28%3A01.316Z%2F2016-12-19T23%3A48%3A01.316Z'
                }
              ],
              'level': 4,
              'device_ip': '127.0.0.1',
              'user_dst': 'Jake',
              'size': 239,
              'bytes': 9702534,
              'domain': '',
              'device_class': 'Firewall',
              'duration_time': 344561,
              'time': 1482190629000,
              'to': '',
              'ip_addr': '76.64.188.48',
              'detector': {
                'device_class': 'Firewall',
                'ip_address': '127.0.0.1',
                'product_name': 'ciscoasa'
              },
              'user': 'Jake',
              'did': 'logd11-114-42'
            }
          ],
          'timestamp': 1482190681000
        }
      },
      'level': 1,
      'matched': [
        'jake',
        '',
        '',
        '',
        '',
        ''
      ],
      'group': '',
      'lookup': {
        'jake': null
      }
    },
    {
      'indicator': {
        'id': '585874b9d848c66ea0375f80',
        'receivedTime': 1482192057909,
        'status': 'GROUPED_IN_INCIDENT',
        'errorMessage': null,
        'originalHeaders': {
          'severity': 5,
          'device_version': '11.0.0000',
          'device_product': 'Event Stream Analysis',
          'signature_id': 'Suspected C&C',
          'model_name': 'UBA-WinAuth',
          'name': 'P2P software as detected by an Intrusion detection device',
          'device_vendor': 'RSA',
          'version': '1',
          'timestamp': 1481882607482
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': 'INC-9',
        'partOfIncident': true,
        'incidentCreated': 1482190647082,
        'timestamp': 1481882607482,
        'alert': {
          'severity': 50,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/device.ip%3D%2Fdate%2F2016-12-19T23%3A50%3A57.907Z%2F2016-12-20T00%3A10%3A57.907Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/ip.src%3D%2Fdate%2F2016-12-19T23%3A50%3A57.907Z%2F2016-12-20T00%3A10%3A57.907Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/ip.dst%3D%2Fdate%2F2016-12-19T23%3A50%3A57.907Z%2F2016-12-20T00%3A10%3A57.907Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/alias.host%3D%22g00gle.com%22%2Fdate%2F2016-12-19T23%3A50%3A57.907Z%2F2016-12-20T00%3A10%3A57.907Z'
            }
          ],
          'host_summary': '-,',
          'user_summary': [
            'MX11CL01$@CORP.EMC.COM'
          ],
          'risk_score': 50,
          'groupby_domain': 'g00gle.com',
          'source': 'Event Stream Analysis',
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              'MX11CL01$@CORP.EMC.COM',
              '',
              'g00gle.com',
              '10.254.219.156',
              '',
              ''
            ]
          ],
          'signature_id': 'Suspected C&C',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'P2P software as detected by an Intrusion detection device',
          'numEvents': 1,
          'groupby_source_ip': '10.254.219.156',
          'groupby_source_username': '',
          'groupby_detector_ip': '',
          'events': [
            {
              'alias_host': 'g00gle.com',
              'reference_id': '4769',
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
                'user': {
                  'username': 'MX11CL01$@CORP.EMC.COM'
                }
              },
              'device_type': 'winevent_nic',
              'source': {
                'device': {
                  'geolocation': {}
                },
                'user': {}
              },
              'type': 'Unknown',
              'enrichment': {
                'rsa_analytics_uba_winauth_failedservers_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_failedserversscore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_normalized_regexoutput': 'USITBRUTSDM1',
                'rsa_analytics_uba_winauth_newserver_cardinality': 2,
                'rsa_analytics_uba_winauth_newdevice_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_newdevicescore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_newdeviceservice_score': false,
                'rsa_analytics_uba_winauth_failedserversscore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_highserverscore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newdevicescore_score': 17.29329329101412,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_highserverscore_score': 17.29329329101412,
                'rsa_analytics_uba_winauth_newserverscore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newserverscore_score': 8.64664664550706,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_failedserversscore_score': 8.64664664550706,
                'rsa_analytics_uba_winauth_newdevice_cardinality': 2,
                'rsa_analytics_uba_winauth_normalized_timestamp': 1482187252000,
                'rsa_analytics_uba_winauth_normalized_hostname': 'USITBRUTSDM1',
                'rsa_analytics_uba_winauth_failedservers_total_access': 2,
                'rsa_analytics_uba_winauth_aggregation_aggregate': 86.46646645507057,
                'rsa_analytics_uba_winauth_aggregation_confidence': 66.66666666666666,
                'rsa_analytics_uba_winauth_highservers_cardinality': 2,
                'rsa_analytics_uba_winauth_newdevicescore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_highservers_total_access': 2,
                'rsa_analytics_uba_winauth_newserverscore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_highservers_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_highserverscore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_highservers_element_access_count': 1,
                'rsa_analytics_uba_winauth_failedservers_cardinality': 2,
                'rsa_analytics_uba_winauth_newserver_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_failedservers_element_access_count': 1
              },
              'hostname': '',
              'event_type': 'Failure Audit',
              'file': '',
              'detected_by': '-,',
              'host_src': '',
              'from': '',
              'event_src': '10.254.219.156',
              'timestamp': 'Thu Jan 01 00:00:00 UTC 1970',
              'event_source_id': 'DEV1-IM-Concentrator_grcrtp_local:1457801992000',
              'related_links': [
                {
                  'type': 'investigate_original_event',
                  'url': ''
                },
                {
                  'type': 'investigate_destination_domain',
                  'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/alias.host%3D%27g00gle.com%27%2Fdate%2F2016-12-19T23%3A50%3A57.907Z%2F2016-12-20T00%3A10%3A57.907Z'
                }
              ],
              'ip_source': '10.254.219.156',
              'service_name': 'USITBRUTSDM1',
              'user_dst': 'MX11CL01$@CORP.EMC.COM',
              'size': 0,
              'domain': 'g00gle.com',
              'to': '',
              'detector': {
                'device_class': '',
                'ip_address': '',
                'product_name': 'winevent_nic'
              },
              'user': 'MX11CL01$@CORP.EMC.COM',
              'event_time': 1482187252000
            }
          ],
          'timestamp': 1481882607482
        }
      },
      'level': 3,
      'matched': [
        '',
        '',
        'g00gle.com',
        '',
        '',
        ''
      ],
      'group': '0',
      'lookup': {}
    },
    {
      'indicator': {
        'id': '5858768fd848c66ea0375f81',
        'receivedTime': 1482192527590,
        'status': 'GROUPED_IN_INCIDENT',
        'errorMessage': null,
        'originalHeaders': {
          'severity': 5,
          'device_version': '11.0.0000',
          'device_product': 'Event Stream Analysis',
          'signature_id': 'Suspected C&C',
          'model_name': 'UBA-WinAuth',
          'name': 'P2P software as detected by an Intrusion detection device',
          'device_vendor': 'RSA',
          'version': '1',
          'timestamp': 1481882607482
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': 'INC-9',
        'partOfIncident': true,
        'incidentCreated': 1482190647082,
        'timestamp': 1481882607482,
        'alert': {
          'severity': 50,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/device.ip%3D%2Fdate%2F2016-12-19T23%3A58%3A47.588Z%2F2016-12-20T00%3A18%3A47.588Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/ip.src%3D%2Fdate%2F2016-12-19T23%3A58%3A47.588Z%2F2016-12-20T00%3A18%3A47.588Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/ip.dst%3D%2Fdate%2F2016-12-19T23%3A58%3A47.588Z%2F2016-12-20T00%3A18%3A47.588Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/alias.host%3D%22g00gle.com%22%2Fdate%2F2016-12-19T23%3A58%3A47.588Z%2F2016-12-20T00%3A18%3A47.588Z'
            }
          ],
          'host_summary': '-,',
          'user_summary': [
            'Jake'
          ],
          'risk_score': 50,
          'groupby_domain': 'g00gle.com',
          'source': 'Event Stream Analysis',
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              'Jake',
              '',
              'g00gle.com',
              '76.64.188.48',
              '',
              ''
            ]
          ],
          'signature_id': 'Suspected C&C',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'P2P software as detected by an Intrusion detection device',
          'numEvents': 1,
          'groupby_source_ip': '76.64.188.48',
          'groupby_source_username': '',
          'groupby_detector_ip': '',
          'events': [
            {
              'alias_host': 'g00gle.com',
              'reference_id': '4769',
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
                'user': {
                  'username': 'Jake'
                }
              },
              'device_type': 'winevent_nic',
              'source': {
                'device': {
                  'geolocation': {}
                },
                'user': {}
              },
              'type': 'Unknown',
              'enrichment': {
                'rsa_analytics_uba_winauth_failedservers_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_failedserversscore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_normalized_regexoutput': 'USITBRUTSDM1',
                'rsa_analytics_uba_winauth_newserver_cardinality': 2,
                'rsa_analytics_uba_winauth_newdevice_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_newdevicescore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_newdeviceservice_score': false,
                'rsa_analytics_uba_winauth_failedserversscore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_highserverscore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newdevicescore_score': 17.29329329101412,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_highserverscore_score': 17.29329329101412,
                'rsa_analytics_uba_winauth_newserverscore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newserverscore_score': 8.64664664550706,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_failedserversscore_score': 8.64664664550706,
                'rsa_analytics_uba_winauth_newdevice_cardinality': 2,
                'rsa_analytics_uba_winauth_normalized_timestamp': 1457801992000,
                'rsa_analytics_uba_winauth_normalized_hostname': 'USITBRUTSDM1',
                'rsa_analytics_uba_winauth_failedservers_total_access': 2,
                'rsa_analytics_uba_winauth_aggregation_aggregate': 86.46646645507057,
                'rsa_analytics_uba_winauth_aggregation_confidence': 66.66666666666666,
                'rsa_analytics_uba_winauth_highservers_cardinality': 2,
                'rsa_analytics_uba_winauth_newdevicescore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_highservers_total_access': 2,
                'rsa_analytics_uba_winauth_newserverscore_ratio': 1.9999996141976426,
                'rsa_analytics_uba_winauth_highservers_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_highserverscore_score': 86.46646645507059,
                'rsa_analytics_uba_winauth_highservers_element_access_count': 1,
                'rsa_analytics_uba_winauth_failedservers_cardinality': 2,
                'rsa_analytics_uba_winauth_newserver_average_cardinality': 1.000000192901216,
                'rsa_analytics_uba_winauth_failedservers_element_access_count': 1
              },
              'hostname': '',
              'event_type': 'Failure Audit',
              'file': '',
              'detected_by': '-,',
              'host_src': '',
              'from': '',
              'event_src': '76.64.188.48',
              'timestamp': 'Thu Jan 01 00:00:00 UTC 1970',
              'event_source_id': 'DEV1-IM-Concentrator_grcrtp_local:1457801992000',
              'related_links': [
                {
                  'type': 'investigate_original_event',
                  'url': ''
                },
                {
                  'type': 'investigate_destination_domain',
                  'url': '/investigation/DEV1-IM-Concentrator_grcrtp_local:1457801992000/navigate/query/alias.host%3D%27g00gle.com%27%2Fdate%2F2016-12-19T23%3A58%3A47.588Z%2F2016-12-20T00%3A18%3A47.588Z'
                }
              ],
              'ip_source': '76.64.188.48',
              'service_name': 'USITBRUTSDM1',
              'user_dst': 'Jake',
              'size': 0,
              'domain': 'g00gle.com',
              'to': '',
              'detector': {
                'device_class': '',
                'ip_address': '',
                'product_name': 'winevent_nic'
              },
              'user': 'Jake',
              'event_time': 1482187252000
            }
          ],
          'timestamp': 1481882607482
        }
      },
      'level': 3,
      'matched': [
        'jake',
        '',
        'g00gle.com',
        '76.64.188.48',
        '',
        ''
      ],
      'group': '0',
      'lookup': {
        'jake': null
      }
    }
  ]
};
