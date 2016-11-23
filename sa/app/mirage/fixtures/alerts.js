export default [
  {
    'id': '57dcda273004ebc5ef83543d',
    '_class': 'com.rsa.asoc.im.commons.domain.Alert',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'originalHeaders': {
      'severity': 5,
      'device_version': '10.4.0000',
      'device_product': 'Event Stream Analysis',
      'signature_id': 'Some rule',
      'name': 'P2P software as detected by an Intrusion detection device',
      'device_vendor': 'RSA',
      'version': 1,
      'timestamp': 1474091559854
    },
    'originalRawAlert': '{\'events\':[{\'timestamp\':1445971625844,\'event_source_id\':\'DEV1-IM-Concentrator.grcrtp.local:50005:29589\',\'alias_host\':\'google.com\',\'ip_source\':\'192.168.1.4\',\'ip_dst\':\'1.1.1.3\',\'client\':\'Microsoft IE 10.0\',\'referer\':\'curl/7.24.0\',\'content\':\'txt\',\'action\':\'GET\',\'service\':80,\'directory\':\'\',\'file\':\'\',\'enrichment\':{\'normalized\':{\'domain\':\'4554mb.ru\',\'timestamp\':1445971625844,\'domain_sip\':\'4554mb.ru2.2.2.2\',\'url\':\'\'},\'new_domain\':{\'age_age\':10000000,\'age_score\':98.7},\'user_agent\':{\'rare_cardinality\':10,\'rare_score\':96.5,\'rare_num_events\':106,\'rare_conditional_cardinality\':2,\'rare_ratio\':0.2,\'rare_ratio_score\':91.2},\'domain\':{\'referer_cardinality\':10,\'referer_score\':96.5,\'referer_num_events\':106,\'referer_conditional_cardinality\':2,\'referer_ratio\':0.2,\'referer_ratio_score\':91.2,\'ua_cardinality\':10,\'ua_score\':96.5,\'ua_num_events\':106,\'ua_conditional_cardinality\':2,\'ua_ratio\':0.2,\'ua_ratio_score\':91.2},\'beaconing\':{\'beaconing_score\':99,\'beaconing_period\':3622},\'smooth\':{\'smooth_beaconing_score\':99.2},\'whois\':{\'estimated_domain_age_days\':2000,\'estimated_domain_validity_days\':3000,\'scaled_age\':90.5,\'scaled_validity\':92.1,\'age_score\':10.3,\'validity_score\':9.1,\'domain_name\':\'4554mb.ru\',\'created_date\':\'23-sep-2015\',\'expires_date\':\'10-dec-2015\',\'updated_date\':\'2015-09-26\',\'is_cached\':false,\'source\':\'DATABASE\'},\'command_control\':{\'aggregate\':90,\'confidence\':100,\'weighted_domain_referer_score\':30,\'weighted_domain_referer_ratio_score\':20,\'weighted_domain_ua_ratio_score\':30,\'weighted_whois_age_score\':10,\'weighted_whois_validity_score\':10}}}]}',
    'originalAlert': {
      'events': [
        {
          'referer': 'curl/7.24.0',
          'event_source_id': 'DEV1-IM-Concentrator.grcrtp.local:50005:29589',
          'alias_host': 'google.com',
          'ip_source': '192.168.1.4',
          'directory': '',
          'ip_dst': '1.1.1.3',
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
              'timestamp': 1474091559854,
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
          'service': 80,
          'client': 'Microsoft IE 10.0',
          'action': 'GET',
          'timestamp': 1474091559854
        }
      ]
    },
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
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A41.159Z%2F2016-09-17T06%3A02%3A41.159Z'
        },
        {
          'type': 'investigate_src_ip',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A41.159Z%2F2016-09-17T06%3A02%3A41.159Z'
        },
        {
          'type': 'investigate_dst_ip',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A41.159Z%2F2016-09-17T06%3A02%3A41.159Z'
        },
        {
          'type': 'investigate_destination_domain',
          'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%22google.com%22%2Fdate%2F2016-09-17T05%3A42%3A41.159Z%2F2016-09-17T06%3A02%3A41.159Z'
        }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 50,
      'groupby_domain': 'google.com',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        [
          '',
          '',
          'google.com',
          '192.168.1.4',
          '1.1.1.3',
          ''
        ]
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
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
              'timestamp': 1474091559854,
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
      'timestamp': 1474091559854
    },
    'partOfIncident': true,
    'incidentId': 'INC-85',
    'incidentCreated': 1474091559854
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-115',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-103',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-86',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-121',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-110',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-120',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-114',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  },
  {
    'id': '57dcda273004ebc5ef83543d',
    'receivedTime': 1474091559854,
    'status': 'GROUPED_IN_INCIDENT',
    'errorMessage': null,
    'originalHeaders': null,
    'originalRawAlert': null,
    'originalAlert': null,
    'alert': {
      'severity': 40,
      'groupby_type': '',
      'related_links': [
        { 'type': 'investigate_session','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/' },
        { 'type': 'investigate_device_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_src_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_dst_ip','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' },
        { 'type': 'investigate_destination_domain','url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z' }
      ],
      'host_summary': '-,',
      'user_summary': [],
      'risk_score': 40,
      'groupby_domain': '4554mb.ru',
      'source': 'Event Stream Analysis',
      'groupby_destination_port': '',
      'groupby_source_country': '',
      'groupby_destination_country': '',
      'relationships': [
        ['','','4554mb.ru','192.168.1.1','2.2.2.2','']
      ],
      'signature_id': 'Some rule',
      'groupby_filename': '',
      'groupby_data_hash': '',
      'groupby_destination_ip': '',
      'name': 'P2P software as detected by an Intrusion detection device',
      'numEvents': 1,
      'groupby_source_ip': '',
      'groupby_source_username': '',
      'groupby_detector_ip': '',
      'timestamp': 1471341807482
    },
    'incidentId': 'INC-111',
    'partOfIncident': true,
    'incidentCreated': 1474091560367,
    'timestamp': 1471341807482
  }

];