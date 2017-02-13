const data = {
  'relatedIndicators': [
    {
      'indicator': {
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
          'user_summary': [

          ],
          'risk_score': 90,
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
              '10.64.188.48',
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
          'groupby_source_ip': '10.64.188.48',
          'groupby_source_username': '',
          'groupby_detector_ip': '',
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
                  'geolocation': {

                  }
                },
                'user': {

                }
              },
              'source': {
                'device': {
                  'geolocation': {

                  }
                },
                'user': {

                }
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
                'rsa_analytics_http-log_c2_normalized_srcip_full_domain': '10.64.188.48_g00gle.com',
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
      'level': 3,
      'matched': [
        '',
        '',
        'g00gle.com',
        '10.64.188.48',
        '3.3.3.3',
        ''
      ],
      'group': '0',
      'lookup': {

      }
    },
    {
      'indicator': {
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
      'level': 1,
      'matched': [
        '',
        '',
        'g00gle.com',
        '',
        '',
        ''
      ],
      'group': '',
      'lookup': {

      }
    },
    {
      'indicator': {
        'id': '586ecfc0ecd25950034e1317',
        'receivedTime': 1483657152534,
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
              'url': '/investigation/10.101.217.47:50005/navigate/query/sessionid%3D198779'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D127.0.0.1%2Fdate%2F2017-01-05T22%3A49%3A12.531Z%2F2017-01-05T23%3A09%3A12.531Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2017-01-05T22%3A49%3A12.531Z%2F2017-01-05T23%3A09%3A12.531Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2017-01-05T22%3A49%3A12.531Z%2F2017-01-05T23%3A09%3A12.531Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%22g00gle.com%22%2Fdate%2F2017-01-05T22%3A49%3A12.531Z%2F2017-01-05T23%3A09%3A12.531Z'
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
                  'geolocation': {

                  }
                },
                'user': {
                  'username': 'Jake'
                }
              },
              'description': 'Network.Connections',
              'device_type': 'ciscoasa',
              'sessionid': 198779,
              'medium': 32,
              'source': {
                'device': {
                  'geolocation': {

                  }
                },
                'user': {

                }
              },
              'rid': 63685,
              'type': 'Network',
              'enrichment': {
                'rsa_analytics_uba-cisco_vpn_normalized_timestamp': 1483657088000000,
                'rsa_analytics_uba-cisco_vpn_rarehostscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_aggregation_aggregate': 49.23879806356344,
                'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 45.11883639059736,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_newispscore_score': 45.11883639059736,
                'rsa_analytics_uba-cisco_vpn_rarehost_total_access': 2,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 5.183635586365643,
                'rsa_analytics_uba-cisco_vpn_rarehost_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_newisp_cardinality': 2,
                'rsa_analytics_uba-cisco_vpn_rarehost_average_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_newispscore_ratio': 2,
                'rsa_analytics_uba-cisco_vpn_newisp_average_cardinality': 1,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 20,
                'rsa_analytics_uba-cisco_vpn_aggregation_weighted_rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 9.023767278119472,
                'rsa_analytics_uba-cisco_vpn_rarehost_element_access_count': 2,
                'rsa_analytics_uba-cisco_vpn_rarehostscore_ratio': 1,
                'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 25.91817793182821,
                'rsa_analytics_uba-cisco_vpn_aggregation_confidence': 80,
                'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 100
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
                  'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%27g00gle.com%27%2Fdate%2F2017-01-05T22%3A49%3A12.531Z%2F2017-01-05T23%3A09%3A12.531Z'
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
      'level': 1,
      'matched': [
        '',
        '',
        'g00gle.com',
        '',
        '',
        ''
      ],
      'group': '',
      'lookup': {

      }
    },
    {
      'indicator': {
        'id': '586ed005ecd25950034e131b',
        'receivedTime': 1483657221358,
        'status': 'NORMALIZED',
        'errorMessage': null,
        'originalHeaders': {
          'severity': 8,
          'device_version': '11.0.FIXME',
          'device_product': 'Event Stream Analysis',
          'signature_id': 'Suspected UBA',
          'model_name': 'UBA-WinAuth',
          'name': 'Suspected UBA',
          'device_vendor': 'RSA',
          'version': 0,
          'timestamp': 1483657221000
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': null,
        'partOfIncident': false,
        'incidentCreated': null,
        'timestamp': 1483657221000,
        'alert': {
          'severity': 80,
          'groupby_type': '',
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/10.101.217.47:50005/navigate/query/sessionid%3D198787'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/device.ip%3D127.0.0.1%2Fdate%2F2017-01-05T22%3A50%3A21.356Z%2F2017-01-05T23%3A10%3A21.356Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.src%3D%2Fdate%2F2017-01-05T22%3A50%3A21.356Z%2F2017-01-05T23%3A10%3A21.356Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/10.101.217.47:50005/navigate/query/ip.dst%3D%2Fdate%2F2017-01-05T22%3A50%3A21.356Z%2F2017-01-05T23%3A10%3A21.356Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%22WINDOWS7MACHINE%22%2Fdate%2F2017-01-05T22%3A50%3A21.356Z%2F2017-01-05T23%3A10%3A21.356Z'
            }
          ],
          'host_summary': 'Windows Hosts-,',
          'user_summary': [
            'Jake'
          ],
          'risk_score': 80,
          'groupby_domain': 'WINDOWS7MACHINE',
          'source': 'Event Stream Analysis',
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              'Jake',
              '',
              'WINDOWS7MACHINE',
              '10.64.188.48',
              '',
              ''
            ]
          ],
          'signature_id': 'Suspected UBA',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'Suspected UBA',
          'numEvents': 1,
          'groupby_source_ip': '',
          'groupby_source_username': '',
          'groupby_detector_ip': '127.0.0.1',
          'events': [
            {
              'event_byte_size': 369,
              'ec_activity': 'Logon',
              'header_id': '0004',
              'alias_host': 'WINDOWS7MACHINE',
              'reference_id': '4624',
              'data': [
                {
                  'filename': '',
                  'size': 981,
                  'hash': ''
                }
              ],
              'event_cat_name': 'User.Activity.Successful Logins',
              'ip_src': '10.64.188.48',
              'destination': {
                'device': {
                  'geolocation': {

                  }
                },
                'user': {
                  'username': 'Jake'
                }
              },
              'description': 'An account was successfully logged on.',
              'device_type': 'winevent_snare',
              'event_source': 'Microsoft-Windows-Security-Auditing',
              'sessionid': 198787,
              'medium': 32,
              'source': {
                'device': {
                  'geolocation': {

                  }
                },
                'user': {

                }
              },
              'rid': 63693,
              'type': 'Network',
              'enrichment': {
                'rsa_analytics_uba_winauth_failedservers_average_cardinality': 1,
                'rsa_analytics_uba_winauth_failedserversscore_score': 63.212055882855765,
                'rsa_analytics_uba_winauth_newserver_cardinality': 1,
                'rsa_analytics_uba_winauth_newdevice_average_cardinality': 1,
                'rsa_analytics_uba_winauth_newdevicescore_score': 63.212055882855765,
                'rsa_analytics_uba_winauth_newdeviceservice_score': false,
                'rsa_analytics_uba_winauth_failedserversscore_ratio': 1,
                'rsa_analytics_uba_winauth_highserverscore_ratio': 1,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newdevicescore_score': 12.642411176571153,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_highserverscore_score': 12.642411176571153,
                'rsa_analytics_uba_winauth_newserverscore_score': 63.212055882855765,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newserverscore_score': 6.321205588285577,
                'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_failedserversscore_score': 6.321205588285577,
                'rsa_analytics_uba_winauth_newdevice_cardinality': 1,
                'rsa_analytics_uba_winauth_normalized_timestamp': 1473691791000,
                'rsa_analytics_uba_winauth_failedservers_total_access': 1,
                'rsa_analytics_uba_winauth_aggregation_aggregate': 63.21205588285576,
                'rsa_analytics_uba_winauth_aggregation_confidence': 66.66666666666666,
                'rsa_analytics_uba_winauth_highservers_cardinality': 1,
                'rsa_analytics_uba_winauth_newdevicescore_ratio': 1,
                'rsa_analytics_uba_winauth_highservers_total_access': 1,
                'rsa_analytics_uba_winauth_newserverscore_ratio': 1,
                'rsa_analytics_uba_winauth_highservers_average_cardinality': 1,
                'rsa_analytics_uba_winauth_highserverscore_score': 63.212055882855765,
                'rsa_analytics_uba_winauth_highservers_element_access_count': 1,
                'rsa_analytics_uba_winauth_failedservers_cardinality': 1,
                'rsa_analytics_uba_winauth_newserver_average_cardinality': 1,
                'rsa_analytics_uba_winauth_failedservers_element_access_count': 1
              },
              'event_type': 'Success Audit',
              'file': '',
              'detected_by': 'Windows Hosts-,',
              'event_computer': 'WINDOWS7MACHINE',
              'from': '',
              'msg_id': 'Security_4624_Microsoft-Windows-Security-Auditing',
              'timestamp': 'Thu Jan 05 22:59:29 UTC 2017',
              'ec_subject': 'User',
              'process': 'Kerberos',
              'event_source_id': 'concentrator',
              'related_links': [
                {
                  'type': 'investigate_original_event',
                  'url': ''
                },
                {
                  'type': 'investigate_destination_domain',
                  'url': '/investigation/10.101.217.47:50005/navigate/query/alias.host%3D%27WINDOWS7MACHINE%27%2Fdate%2F2017-01-05T22%3A50%3A21.356Z%2F2017-01-05T23%3A10%3A21.356Z'
                }
              ],
              'level': 1,
              'ec_theme': 'Authentication',
              'device_group': 'All Windows Event Source(s)',
              'logon_type': '3',
              'device_ip': '127.0.0.1',
              'event_desc': 'An account was successfully logged on.',
              'user_dst': 'Jake',
              'size': 981,
              'domain': 'WINDOWS7MACHINE',
              'device_class': 'Windows Hosts',
              'time': 1483657169000,
              'to': '',
              'category': 'Logon',
              'event_user': '-',
              'ec_outcome': 'Success',
              'detector': {
                'device_class': 'Windows Hosts',
                'ip_address': '127.0.0.1',
                'product_name': 'winevent_snare'
              },
              'user': 'Jake',
              'event_time': 1473691791,
              'did': 'logd11-114-42'
            }
          ],
          'timestamp': 1483657221000
        }
      },
      'level': 1,
      'matched': [
        '',
        '',
        '',
        '10.64.188.48',
        '',
        ''
      ],
      'group': '',
      'lookup': {

      }
    },
    {
      'indicator': {
        'id': '586ed1d8ecd25950034e131c',
        'receivedTime': 1483657688645,
        'status': 'NORMALIZED',
        'errorMessage': null,
        'originalHeaders': {
          'severity': 5,
          'device_version': '11.0.0000',
          'device_product': 'ECAT',
          'signature_id': 'ModuleIOC',
          'name': 'ModuleIOC',
          'device_vendor': 'RSA',
          'version': '1',
          'timestamp': 1483610607482
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': null,
        'partOfIncident': false,
        'incidentCreated': null,
        'timestamp': 1483610607482,
        'alert': {
          'severity': 30,
          'groupby_type': 'Instant IOC',
          'related_links': [
            {
              'type': 'investigate_ecat'
            },
            {
              'url': 'ecatui://26C5C21F-4DA8-3A00-437C-AB7444987430'
            }
          ],
          'host_summary': '',
          'user_summary': [

          ],
          'risk_score': 30,
          'groupby_domain': '',
          'source': 'ECAT',
          'type': [
            'Instant IOC'
          ],
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              '',
              'WINDOWS7MACHINE',
              '',
              '10.64.188.48',
              '',
              'de9f2c7fd25e1b3a fad3e85a 0bd17d9b 100db4b3'
            ]
          ],
          'signature_id': 'ModuleIOC',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'ModuleIOC',
          'numEvents': 1,
          'groupby_source_ip': '',
          'groupby_source_username': '',
          'groupby_detector_ip': '10.64.188.48',
          'events': [
            {
              'score': 15,
              'related_links': [
                {
                  'type': 'investigate_ecat',
                  'url': 'ecatui://26C5C21F-4DA8-3A00-437C-AB7444987430'
                }
              ],
              'file': 'filename.exe',
              'description': 'ModuleIOC',
              'source': {
                'user': {

                }
              },
              'type': 'Instant IOC',
              'detector': {
                'dns_hostname': 'WINDOWS7MACHINE',
                'os': 'Windows 7',
                'mac_address': '11-11-11-11-11-11-11-11',
                'ecat_agent_id': '26C5C21F-4DA8-3A00-437C-AB7444987430',
                'ip_address': '10.64.188.48'
              },
              'timestamp': ''
            }
          ],
          'timestamp': 1483610607482
        }
      },
      'level': 1,
      'matched': [
        '',
        '',
        '',
        '10.64.188.48',
        '',
        ''
      ],
      'group': '',
      'lookup': {

      }
    },
    {
      'indicator': {
        'id': '586ed614ecd25950034e131d',
        'receivedTime': 1483658772167,
        'status': 'GROUPED_IN_INCIDENT',
        'errorMessage': null,
        'originalHeaders': {
          'severity': 9,
          'device_version': '11.0.0000',
          'device_product': 'ECAT',
          'signature_id': 'ModuleIOC',
          'name': 'ModuleIOC',
          'device_vendor': 'RSA',
          'version': '1',
          'timestamp': 1483610607482
        },
        'originalRawAlert': null,
        'originalAlert': null,
        'incidentId': 'INC-19',
        'partOfIncident': true,
        'incidentCreated': 1483658774531,
        'timestamp': 1483610607482,
        'alert': {
          'severity': 90,
          'groupby_type': 'Instant IOC',
          'related_links': [
            {
              'type': 'investigate_ecat'
            },
            {
              'url': 'ecatui://26C5C21F-4DA8-3A00-437C-AB7444987430'
            }
          ],
          'host_summary': '',
          'user_summary': [

          ],
          'risk_score': 90,
          'groupby_domain': '',
          'source': 'ECAT',
          'type': [
            'Instant IOC'
          ],
          'groupby_destination_port': '',
          'groupby_source_country': '',
          'groupby_destination_country': '',
          'relationships': [
            [
              '',
              'WINDOWS7MACHINE',
              '',
              '10.64.188.48',
              '',
              'de9f2c7fd25e1b3a fad3e85a 0bd17d9b 100db4b3'
            ]
          ],
          'signature_id': 'ModuleIOC',
          'groupby_filename': '',
          'groupby_data_hash': '',
          'groupby_destination_ip': '',
          'name': 'ModuleIOC',
          'numEvents': 1,
          'groupby_source_ip': '',
          'groupby_source_username': '',
          'groupby_detector_ip': '10.64.188.48',
          'events': [
            {
              'score': 1024,
              'related_links': [
                {
                  'type': 'investigate_ecat',
                  'url': 'ecatui://26C5C21F-4DA8-3A00-437C-AB7444987430'
                }
              ],
              'file': 'filename.exe',
              'description': 'ModuleIOC',
              'source': {
                'user': {

                }
              },
              'type': 'Instant IOC',
              'detector': {
                'dns_hostname': 'WINDOWS7MACHINE',
                'os': 'Windows 7',
                'mac_address': '11-11-11-11-11-11-11-11',
                'ecat_agent_id': '26C5C21F-4DA8-3A00-437C-AB7444987430',
                'ip_address': '10.64.188.48'
              },
              'timestamp': ''
            }
          ],
          'timestamp': 1483610607482
        }
      },
      'level': 1,
      'matched': [
        '',
        '',
        '',
        '10.64.188.48',
        '',
        ''
      ],
      'group': '',
      'lookup': {

      }
    }
  ]
};

export default {
  subscriptionDestination: '/user/queue/incident/storyline',
  requestDestination: '/ws/response/incident/storyline',
  message(/* frame */) {
    return {
      data
    };
  }
};