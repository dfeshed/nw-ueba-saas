export default {
  'relatedIndicators': [{
    'level': 3,
    'indicator': {
      'id': '57c9dc2f300458fc5032964b',
      'receivedTime': 1472846895049,
      'status': 'GROUPED_IN_INCIDENT',
      'errorMessage': null,
      'originalHeaders': {
        'severity': 7,
        'device_version': '10.4.0000',
        'device_product': 'Event Stream Analysis',
        'signature_id': 'Suspected C&C',
        'name': 'Suspected command and control communication with 4554mb.ru',
        'device_vendor': 'RSA',
        'version': 1,
        'timestamp': '2016-08-17T10:03:27.482Z'
      },
      'originalRawAlert': null,
      'originalAlert': null,
      'alert': {
        'events': [
          {
            'referer': 'curl/7.24.0',
            'alias_host': 'google.com',
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
              'command_control': {
                'weighted_domain_ua_ratio_score': 30,
                'weighted_domain_referer_ratio_score': 20,
                'weighted_domain_referer_score': 30,
                'confidence': 100,
                'weighted_whois_age_score': 10,
                'weighted_whois_validity_score': 10,
                'aggregate': 90
              },
              'whois': {
                'scaled_validity': 92.1,
                'domain_name': '4554mb.ru',
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
              'normalized': {
                'domain': '4554mb.ru',
                'url': '',
                'domain_sip': '4554mb.ru2.2.2.2'
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
            'detected_by': '-,',
            'client': 'Microsoft IE 10.0',
            'action': 'GET',
            'from': '',
            'timestamp': 'Tue Oct 27 11:47:05 PDT 2015',
            'event_source_id': 'DEV1-IM-Concentrator.grcrtp.local:50005:29589',
            'related_links': [
              {
                'type': 'investigate_original_event',
                'url': ''
              },
              {
                'type': 'investigate_destination_domain',
                'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%27google.com%27%2Fdate%2F2016-09-17T05%3A42%3A41.159Z%2F2016-09-17T06%3A02%3A41.159Z'
              }
            ],
            'ip_source': '192.168.1.4',
            'ip_dst': '1.1.1.3',
            'size': 0,
            'service': 80,
            'domain': 'google.com',
            'to': '',
            'detector': {
              'device_class': '',
              'ip_address': '',
              'product_name': ''
            },
            'user': ''
          }
        ],

        'severity': 50,
        'groupby_type': '',
        'related_links': [
          {
            'type': 'investigate_session',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/'
          },
          {
            'type': 'investigate_device_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-02T19%3A58%3A15.044Z%2F2016-09-02T20%3A18%3A15.044Z'
          },
          {
            'type': 'investigate_src_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-02T19%3A58%3A15.044Z%2F2016-09-02T20%3A18%3A15.044Z'
          },
          {
            'type': 'investigate_dst_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-02T19%3A58%3A15.044Z%2F2016-09-02T20%3A18%3A15.044Z'
          },
          {
            'type': 'investigate_destination_domain',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-02T19%3A58%3A15.044Z%2F2016-09-02T20%3A18%3A15.044Z'
          }
        ],
        'host_summary': '-,',
        'user_summary': [],
        'risk_score': 50,
        'groupby_domain': '4554mb.ru',
        'source': 'Event Stream Analysis',
        'groupby_destination_port': '',
        'groupby_source_country': '',
        'groupby_destination_country': '',
        'relationships': [
          [
            '',
            '',
            '4554mb.ru',
            '2.2.2.2',
            '3.3.3.3',
            ''
          ]
        ],
        'signature_id': 'Suspected C&C',
        'groupby_filename': '',
        'groupby_data_hash': '',
        'groupby_destination_ip': '',
        'name': 'Suspected command and control communication with 4554mb.ru',
        'numEvents': 1,
        'groupby_source_ip': '10.101.47.66',
        'groupby_source_username': '',
        'groupby_detector_ip': '',
        'timestamp': 1545940207482
      },
      'incidentId': 'INC-149',
      'partOfIncident': true,
      'incidentCreated': 1472846899949,
      'timestamp': 1445940207482
    }
  },
  {
    'level': 3,
    'indicator': {
      'id': '57c9dc2d300458fc5032964a',
      'receivedTime': 1472846893180,
      'status': 'GROUPED_IN_INCIDENT',
      'errorMessage': null,
      'originalHeaders': {
        'severity': 7,
        'device_version': '10.4.0000',
        'device_product': 'Event Stream Analysis',
        'signature_id': 'Some rule',
        'name': 'P2P software as detected by an Intrusion detection device',
        'device_vendor': 'RSA',
        'version': 1,
        'timestamp': '2016-08-17T10:03:27.482Z'
      },
      'originalRawAlert': null,
      'originalAlert': null,
      'alert': {
        'severity': 50,
        'groupby_type': '',
        'related_links': [{
          'type': 'investigate_session',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/'
        },
        {
          'type': 'investigate_device_ip',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-02T19%3A58%3A13.175Z%2F2016-09-02T20%3A18%3A13.175Z'
        },
        {
          'type': 'investigate_src_ip',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-02T19%3A58%3A13.175Z%2F2016-09-02T20%3A18%3A13.175Z'
        },
        {
          'type': 'investigate_dst_ip',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-02T19%3A58%3A13.175Z%2F2016-09-02T20%3A18%3A13.175Z'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-02T19%3A58%3A13.175Z%2F2016-09-02T20%3A18%3A13.175Z'
        }],
        'host_summary': '-,',
        'user_summary': [],
        'risk_score': 50,
        'groupby_domain': '4554mb.ru',
        'source': 'Event Stream Analysis',
        'groupby_destination_port': '',
        'groupby_source_country': '',
        'groupby_destination_country': '',
        'relationships': [
          [
            '',
            '',
            '4554mb.ru',
            '2.2.2.2',
            '3.3.3.3',
            ''
          ]
        ],
        'signature_id': 'Some rule',
        'groupby_filename': '',
        'groupby_data_hash': '',
        'groupby_destination_ip': '',
        'name': 'P2P software as detected by an Intrusion detection device',
        'numEvents': 1,
        'groupby_source_ip': '10.22.47.106',
        'groupby_source_username': '',
        'groupby_detector_ip': '',
        'timestamp': 1450940207482
      },
      'incidentId': 'INC-148',
      'partOfIncident': true,
      'incidentCreated': 1472846894946,
      'timestamp': 1445940207482
    }
  },
  {
    'level': 3,
    'indicator': {
      'id': '57c9da86300458fc50329649',
      'receivedTime': 1472846470829,
      'status': 'GROUPED_IN_INCIDENT',
      'errorMessage': null,
      'originalHeaders': {
        'severity': 7,
        'device_version': '10.4.0000',
        'device_product': 'ECAT',
        'signature_id': 'ModuleIOC',
        'name': 'ModuleIOC',
        'device_vendor': 'RSA',
        'version': 1,
        'timestamp': '2016-08-17T10:03:27.482Z'
      },
      'originalRawAlert': null,
      'originalAlert': null,
      'alert': {
        'severity': 50,
        'groupby_type': '',
        'related_links': [
          {
            'type': 'investigate_session',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/'
          },
          {
            'type': 'investigate_device_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-02T19%3A51%3A10.824Z%2F2016-09-02T20%3A11%3A10.824Z'
          },
          {
            'type': 'investigate_src_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-02T19%3A51%3A10.824Z%2F2016-09-02T20%3A11%3A10.824Z'
          },
          {
            'type': 'investigate_dst_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-02T19%3A51%3A10.824Z%2F2016-09-02T20%3A11%3A10.824Z'
          },
          {
            'type': 'investigate_destination_domain',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-02T19%3A51%3A10.824Z%2F2016-09-02T20%3A11%3A10.824Z'
          }
        ],
        'host_summary': '-,',
        'user_summary': [

        ],
        'risk_score': 50,
        'groupby_domain': '4554mb.ru',
        'source': 'Event Stream Analysis',
        'groupby_destination_port': '',
        'groupby_source_country': '',
        'groupby_destination_country': '',
        'relationships': [
          [
            '',
            '',
            '4554mb.ru',
            '2.2.2.2',
            '3.3.3.3',
            ''
          ]
        ],
        'signature_id': 'ModuleIOC',
        'groupby_filename': '',
        'groupby_data_hash': '',
        'groupby_destination_ip': '',
        'name': 'P2P software as detected by an Intrusion detection device',
        'numEvents': 1,
        'groupby_source_ip': '10.121.147.66',
        'groupby_source_username': '',
        'groupby_detector_ip': '',
        'timestamp': 1445940207482
      },
      'incidentId': 'INC-147',
      'partOfIncident': true,
      'incidentCreated': 1472846474842,
      'timestamp': 1445940207482
    }
  },
  {
    'level': 3,
    'indicator': {
      'id': '57c9da85300458fc50329648',
      'receivedTime': 1472846469169,
      'status': 'GROUPED_IN_INCIDENT',
      'errorMessage': null,
      'originalHeaders': {
        'severity': 7,
        'device_version': '10.4.0000',
        'device_product': 'Event Stream Analysis',
        'signature_id': 'Suspected UBA',
        'name': 'Suspected UBA',
        'device_vendor': 'RSA',
        'version': 1,
        'timestamp': '2016-08-17T10:03:27.482Z'
      },
      'originalRawAlert': null,
      'originalAlert': null,
      'alert': {
        'severity': 50,
        'groupby_type': '',
        'related_links': [
          {
            'type': 'investigate_session',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/'
          },
          {
            'type': 'investigate_device_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-02T19%3A51%3A09.158Z%2F2016-09-02T20%3A11%3A09.158Z'
          },
          {
            'type': 'investigate_src_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-02T19%3A51%3A09.158Z%2F2016-09-02T20%3A11%3A09.158Z'
          },
          {
            'type': 'investigate_dst_ip',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-02T19%3A51%3A09.158Z%2F2016-09-02T20%3A11%3A09.158Z'
          },
          {
            'type': 'investigate_destination_domain',
            'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local: 50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-02T19%3A51%3A09.158Z%2F2016-09-02T20%3A11%3A09.158Z'
          }
        ],
        'host_summary': '-,',
        'user_summary': [],
        'risk_score': 50,
        'groupby_domain': '4554mb.ru',
        'source': 'Event Stream Analysis',
        'groupby_destination_port': '',
        'groupby_source_country': '',
        'groupby_destination_country': '',
        'relationships': [
          [
            '',
            '',
            '4554mb.ru',
            '2.2.2.2',
            '3.3.3.3',
            ''
          ]
        ],
        'signature_id': 'Suspected UBA',
        'groupby_filename': '',
        'groupby_data_hash': '',
        'groupby_destination_ip': '',
        'name': '',
        'numEvents': 1,
        'groupby_source_ip': '10.101.47.66',
        'groupby_source_username': '',
        'groupby_detector_ip': '',
        'timestamp': 1445940201482
      },
      'incidentId': 'INC-146',
      'partOfIncident': true,
      'incidentCreated': 1472846469875,
      'timestamp': 1445940207482
    }
  }]
};
