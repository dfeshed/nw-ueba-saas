import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-incident-detail-storyline', 'Integration | Component | rsa respond/incident detail/storyline', {
  integration: true,
  beforeEach() {
    this.set('i18n', this.container.lookup('service:i18n'));
  }
});


test('it renders', function(assert) {
  const details = { 'indicators': [
    {
      'group': '',
      'indicator': {
        'alert': {
          'events': [{
            'action': 'GET',
            'alias_host': '4554mb.ru',
            'client': 'Microsoft IE 10.0',
            'content': 'txt',
            'data': [{
              'filename': '',
              'hash': '',
              'size': 0
            }],
            'destination': {
              'device': {
                'geolocation': {}
              },
              'user': {}
            },
            'detected_by': '-,',
            'detector': {
              'device_class': '',
              'ip_address': '',
              'product_name': ''
            },
            'directory': '',
            'domain': '4554mb.ru',
            'enrichment': {
              'rsa_analytics_http-packet_c2_newdomain_score': 85.53810102910933,
              'rsa_analytics_http-packet_c2_normalized_srcip_full_domain': '10.40.12.30_evil1-beacon45s.com',
              'rsa_analytics_http-packet_c2_referer_score': 64.4036421083141,
              'rsa_analytics_http-packet_c2_ua_ratio_score': 100.0,
              'rsa_analytics_http-packet_c2_whois_estimated_domain_age_days': 182,
              'rsa_analytics_http-packet_c2_command_control_confidence': 69.0,
              'rsa_analytics_http-packet_c2_normalized_timestamp': 1479162482000000,
              'rsa_analytics_http-packet_c2_referer_cond_cardinality': 3,
              'rsa_analytics_http-packet_c2_ua_num_events': 13,
              'rsa_analytics_http-packet_c2_referer_ratio': 100.0,
              'rsa_analytics_http-packet_c2_beaconing_score': 100.0,
              'rsa_analytics_http-packet_c2_useragent_score': 90.4837418035959,
              'rsa_analytics_http-packet_c2_newdomain_age': 25,
              'rsa_analytics_http-packet_c2_whois_scaled_age': 10.0,
              'rsa_analytics_http-packet_c2_ua_score': 67.0320046035639,
              'rsa_analytics_http-packet_c2_normalized_domain': 'evil1-beacon45s.com',
              'rsa_analytics_http-packet_c2_newdomain_num_events': 17,
              'rsa_analytics_http-packet_c2_whois_validity_score': 83.5030897216977,
              'rsa_analytics_http-packet_c2_command_control_aggregate': 90.7626911163496,
              'rsa_analytics_http-packet_c2_whois_age_score': 86.639009496887,
              'rsa_analytics_http-packet_c2_normalized_user_agent': 'Mozilla/5.0',
              'rsa_analytics_http-packet_c2_whois_scaled_validity': 10.0,
              'rsa_analytics_http-packet_c2_referer_ratio_score': 90.0,
              'rsa_analytics_http-packet_c2_whois_estimated_domain_validity_days': 182,
              'rsa_analytics_http-packet_c2_referer_num_events': 13,
              'rsa_analytics_http-packet_c2_whois_domain_not_found_by_whois': true,
              'rsa_analytics_http-packet_c2_smooth_score': 88.0,
              'rsa_analytics_http-packet_c2_referer_cardinality': 3,
              'rsa_analytics_http-packet_c2_command_control_weighted_rsa_analytics_http-packet_c2_whois_validity_score': 2.50509269165093,
              'rsa_analytics_http-packet_c2_ua_ratio': 100.0,
              'rsa_analytics_http-packet_c2_ua_cond_cardinality': 3,
              'rsa_analytics_http-packet_c2_command_control_weighted_rsa_analytics_http-packet_c2_whois_age_score': 38.1211641786303,
              'rsa_analytics_http-packet_c2_useragent_cardinality': 3,
              'rsa_analytics_http-packet_c2_useragent_num_events': 17,
              'rsa_analytics_http-packet_c2_ua_cardinality': 3,
              'rsa_analytics_http-packet_c2_command_control_weighted_rsa_analytics_http-packet_c2_referer_ratio_score': 22.0,
              'rsa_analytics_http-packet_c2_beaconing_period': 45000000,
              'rsa_analytics_http-packet_c2_normalized_full_domain': 'evil1-beacon45s.com'
            },
            'event_source_id': 'DEV1-IM-Concentrator.grcrtp.local:50005:29589',
            'file': '',
            'from': '',
            'ip_dst': '2.2.2.2',
            'ip_source': '192.168.1.1',
            'referer': 'curl/7.24.0',
            'related_links': [
              {
                'type': 'investigate_original_event',
                'url': ''
              },
              {
                'type': 'investigate_destination_domain',
                'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%274554mb.ru%27%2Fdate%2F2016-09-17T05%3A42%3A42.048Z%2F2016-09-17T06%3A02%3A42.048Z'
              }
            ],
            'service': 80,
            'size': 0,
            'source': {
              'device': {
                'geolocation': {}
              },
              'user': {}
            },
            'timestamp': 'Tue Oct 27 11:47:05 PDT 2015',
            'to': '',
            'type': 'Unknown',
            'user': ''
          }],
          'groupby_data_hash': '',
          'groupby_destination_country': '',
          'groupby_destination_ip': '',
          'groupby_destination_port': '',
          'groupby_detector_ip': '',
          'groupby_domain': '4554mb.ru',
          'groupby_filename': '',
          'groupby_source_country': '',
          'groupby_source_ip': ['10.101.2.3'],
          'groupby_source_username': '',
          'groupby_type': '',
          'host_summary': '-,',
          'name': 'Suspected command and control communication with 4554mb.ru',
          'numEvents': 1,
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-09-17T05%3A42%3A42.048Z%2F2016-09-17T06%3A02%3A42.048Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-09-17T05%3A42%3A42.048Z%2F2016-09-17T06%3A02%3A42.048Z'
            },
            {
              'type': 'investigate_dst_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-09-17T05%3A42%3A42.048Z%2F2016-09-17T06%3A02%3A42.048Z'
            },
            {
              'type': 'investigate_destination_domain',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-09-17T05%3A42%3A42.048Z%2F2016-09-17T06%3A02%3A42.048Z'
            }
          ],
          'relationships': [ [
            '',
            '',
            '4554mb.ru',
            '192.168.1.1',
            '2.2.2.2',
            ''
          ] ],
          'risk_score': 70,
          'severity': 70,
          'signature_id': '1234-5678',
          'model_name': 'C2-Packet',
          'source': 'Event Stream Analysis',
          'timestamp': 1448021299482,
          'user_summary': []
        },
        'errorMessage': null,
        'id': '57dcda2a3004ebc5ef835440',
        'incidentCreated': 1474091565405,
        'incidentId': 'INC-296',
        'originalAlert': null,
        'originalHeaders': {
          'device_product': 'Event Stream Analysis',
          'device_vendor': 'RSA',
          'device_version': '10.4.0000',
          'name': 'Suspected command and control communication with 4554mb.ru',
          'severity': 7,
          'signature_id': '1234-5678',
          'model_name': 'C2-Packet',
          'timestamp': 1448021299482,
          'version': 1
        },
        'originalRawAlert': null,
        'partOfIncident': true,
        'receivedTime': 1474091562050,
        'status': 'GROUPED_IN_INCIDENT',
        'timestamp': 1448021299482
      },
      'level': 3,
      'lookup': {},
      'matched': [
        '',
        '',
        '4554mb.ru',
        '192.168.1.1',
        '2.2.2.2',
        ''
      ]
    },
    {
      'group': '0',
      'indicator': {
        'alert': {
          'events': [{
            'action': 'GET',
            'alias_host': '4554mb.ru',
            'client': 'Microsoft IE 10.0',
            'content': 'txt',
            'data': [{
              'filename': '',
              'hash': '',
              'size': 0
            }],
            'destination': {
              'device': {
                'geolocation': {}
              },
              'user': {}
            },
            'detected_by': '-,',
            'detector': { 'device_class': '',
              'ip_address': '',
              'product_name': ''
            },
            'directory': '',
            'domain': '4554mb.ru',
            'enrichment': {
              'rsa_analytics_http-log_c2_command_control_weighted_rsa_analytics_http-log_c2_referer_ratio_score': 22.0,
              'rsa_analytics_http-log_c2_normalized_timestamp': 1464696015000,
              'rsa_analytics_http-log_c2_whois_estimated_domain_age_days': 182,
              'rsa_analytics_http-log_c2_whois_estimated_domain_validity_days': 182,
              'rsa_analytics_http-log_c2_referer_ratio_score': 10.0,
              'rsa_analytics_http-log_c2_newdomain_age': 0,
              'rsa_analytics_http-log_c2_command_control_aggregate': 96.7626911163496,
              'rsa_analytics_http-log_c2_normalized_user_agent': 'Mozilla/5.0 (Linux) Gecko Iceweasel (Debian) Mnenhy',
              'rsa_analytics_http-log_c2_ua_ratio_score': 90.0,
              'rsa_analytics_http-log_c2_ua_ratio': 100.0,
              'rsa_analytics_http-log_c2_referer_ratio': 100.0,
              'rsa_analytics_http-log_c2_normalized_srcip_full_domain': '10.10.0.0_mail.iyi.tt',
              'rsa_analytics_http-log_c2_ua_cond_cardinality': 2,
              'rsa_analytics_http-log_c2_newdomain_score': 100.0,
              'rsa_analytics_http-log_c2_whois_validity_score': 83.5030897216977,
              'rsa_analytics_http-log_c2_useragent_score': 95.1229424500714,
              'rsa_analytics_http-log_c2_newdomain_num_events': 287,
              'rsa_analytics_http-log_c2_referer_num_events': 282,
              'rsa_analytics_http-log_c2_useragent_cardinality': 2,
              'rsa_analytics_http-log_c2_ua_score': 81.8730753077982,
              'rsa_analytics_http-log_c2_smooth_score': 77.1475732083557,
              'rsa_analytics_http-log_c2_referer_cardinality': 2,
              'rsa_analytics_http-log_c2_whois_domain_not_found_by_whois': false,
              'rsa_analytics_http-log_c2_referer_cond_cardinality': 2,
              'rsa_analytics_http-log_c2_whois_scaled_age': 10.0,
              'rsa_analytics_http-log_c2_command_control_confidence': 69.0,
              'rsa_analytics_http-log_c2_command_control_weighted_rsa_analytics_http-log_c2_whois_age_score': 38.1211641786303,
              'rsa_analytics_http-log_c2_beaconing_score': 24.6912220309873,
              'rsa_analytics_http-log_c2_normalized_full_domain': 'mail.iyi.tt',
              'rsa_analytics_http-log_c2_command_control_weighted_rsa_analytics_http-log_c2_whois_validity_score': 2.50509269165093,
              'rsa_analytics_http-log_c2_whois_age_score': 86.639009496887,
              'rsa_analytics_http-log_c2_beaconing_period': 26600,
              'rsa_analytics_http-log_c2_referer_score': 70.2518797962478,
              'rsa_analytics_http-log_c2_ua_num_events': 282,
              'rsa_analytics_http-log_c2_useragent_num_events': 288,
              'rsa_analytics_http-log_c2_whois_scaled_validity': 10.0,
              'rsa_analytics_http-log_c2_ua_cardinality': 2,
              'rsa_analytics_http-log_c2_normalized_domain': 'iyi.tt'
            },
            'event_source_id': 'DEV1-IM-Concentrator.grcrtp.local:50005:29589',
            'file': '',
            'from': '',
            'referer': 'curl/7.24.0',
            'related_links': [
              {
                'type': 'investigate_original_event',
                'url': ''
              },
              {
                'type': 'investigate_destination_domain',
                'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%274554mb.ru%27%2Fdate%2F2016-10-25T20%3A24%3A57.310Z%2F2016-10-25T20%3A44%3A57.310Z'
              }
            ],
            'service': 80,
            'size': 0,
            'source': {
              'device': {
                'geolocation': {}
              },
              'user': {}
            },
            'timestamp': 'Tue May 19 05:00:00 PDT 2015',
            'to': '',
            'type': 'Unknown',
            'user': ''
          }],
          'groupby_data_hash': '',
          'groupby_destination_country': '',
          'groupby_destination_ip': '',
          'groupby_destination_port': '',
          'groupby_detector_ip': '',
          'groupby_domain': '4554mb.ru',
          'groupby_filename': '',
          'groupby_source_country': '',
          'groupby_source_ip': ['10.101.2.3', '10.20.2.19'],
          'groupby_source_username': '',
          'groupby_type': '',
          'host_summary': '-,',
          'name': 'P2P software as detected by an Intrusion detection device',
          'numEvents': 1,
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/'
            },
            {
              'type': 'investigate_device_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-10-25T20%3A24%3A57.310Z%2F2016-10-25T20%3A44%3A57.310Z'
            },
            {
              'type': 'investigate_src_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-10-25T20%3A24%3A57.310Z%2F2016-10-25T20%3A44%3A57.310Z'
            },
            { 'type': 'investigate_dst_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-10-25T20%3A24%3A57.310Z%2F2016-10-25T20%3A44%3A57.310Z'
            },
            { 'type': 'investigate_destination_domain',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-10-25T20%3A24%3A57.310Z%2F2016-10-25T20%3A44%3A57.310Z'
            }
          ],
          'relationships': [[
            '',
            '',
            '4554mb.ru',
            '',
            '',
            ''
          ]],
          'risk_score': 50,
          'severity': 50,
          'signature_id': '4567-5678',
          'model_name': 'C2-Log',
          'source': 'Event Stream Analysis',
          'timestamp': 1448021207482,
          'user_summary': []
        },
        'errorMessage': null,
        'id': '580fc1f130041168fd23c781',
        'incidentCreated': 1448021207482,
        'incidentId': 'INC-9',
        'originalAlert': null,
        'originalHeaders': {
          'device_product': 'Event Stream Analysis',
          'device_vendor': 'RSA',
          'device_version': '10.4.0000',
          'name': 'P2P software as detected by an Intrusion detection device',
          'severity': 5,
          'signature_id': '4567-5678',
          'model_name': 'C2-Log',
          'timestamp': 1448021207482,
          'version': 1
        },
        'originalRawAlert': null,
        'partOfIncident': true,
        'receivedTime': 1477427697316,
        'status': 'GROUPED_IN_INCIDENT',
        'timestamp': 1438021207482
      },
      'level': 1,
      'lookup': {},
      'matched': [
        '',
        '',
        '4554mb.ru',
        '',
        '',
        ''
      ]
    },
    {
      'group': '',
      'indicator': {
        'alert': {
          'agentid': '26C5C21F-4DA8-3A00-437C-AB7444987430',
          'shost': 'INENDEBS1L2C',
          'src': '192.168.1.1',
          'smac': '11-11-11-11-11-11-11-11',
          'fname': 'filename.exe',
          'fsize': '23562',
          'fileHash': 'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3',
          'instantIOCName': 'TestIOC',
          'instantIOCLevel': '3',
          'OPSWATResult': 'OPSWAT result here',
          'YARAResult': 'N YARA rules matched',
          'Bit9Status': 'bad',
          'moduleScore': '1-2-3-4',
          'moduleSignature': 'ABC Inc.',
          'os': 'Windows 7',
          'md5sum': '0x00000000000000000000000000000000',
          'machineScore': 1024,
          'related_links': [
            {
              'type': 'investigate_ecat'
            },
            {
              'url': 'ecatui://26C5C21F-4DA8-3A00-437C-AB7444987430'
            }
          ],
          'relationships': [[
            '',
            'INENDEBS1L2C',
            '',
            '192.168.1.0',
            '',
            'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3'
          ]],
          'risk_score': 90,
          'severity': 90,
          'signature_id': 'ModuleIOC',
          'source': 'ECAT',
          'timestamp': 1448000000000,
          'type': [ 'Instant IOC' ],
          'user_summary': []
        },
        'errorMessage': null,
        'id': '5833fee2a7c89226086a0956',
        'incidentCreated': null,
        'incidentId': null,
        'originalAlert': null,
        'originalHeaders': {
          'device_product': 'ECAT',
          'device_vendor': 'RSA',
          'device_version': '4.0.0',
          'name': 'ModuleIOC',
          'severity': 5,
          'signature_id': 'ModuleIOC',
          'timestamp': 1448000000000,
          'version': 1
        },
        'originalRawAlert': null,
        'partOfIncident': false,
        'receivedTime': 1479802594642,
        'status': 'NORMALIZED',
        'timestamp': 1448000000000
      },
      'level': 1,
      'lookup': {
        'inendebs1l2c': [[
          'ip.src',
          '192.168.1.1',
          'ip2host'
        ]]
      },
      'matched': [
        '',
        'inendebs1l2c',
        '',
        '',
        '',
        ''
      ]
    },
    {
      'group': '',
      'indicator': {
        'alert': {
          'events': [{
            'action': 'GET',
            'alias_host': '4554mb.ru',
            'client': 'Microsoft IE 10.0',
            'content': 'txt',
            'data': [{
              'filename': '',
              'hash': '',
              'size': 0
            }],
            'destination': {
              'device': {
                'geolocation': {}
              },
              'user': {}
            },
            'detected_by': '-,',
            'detector': {
              'device_class': '',
              'ip_address': '',
              'product_name': ''
            },
            'directory': '',
            'domain': '4554mb.ru',
            'user_dst': 'MX11CL01$@CORP.EMC.COM',
            'enrichment': {
              'rsa_analytics_uba_winauth_device_exists': false,
              'rsa_analytics_uba_winauth_failedservers_average_cardinality': 1.00000115740674,
              'rsa_analytics_uba_winauth_failedserversscore_score': 66.466440348745,
              'rsa_analytics_uba_winauth_normalized_regexoutput': 'USITBRUTSDM1',
              'rsa_analytics_uba_winauth_newserver_cardinality': 2.0,
              'rsa_analytics_uba_winauth_newdevice_average_cardinality': 1.00000115740674,
              'rsa_analytics_uba_winauth_newdevicescore_score': 86.466440348745,
              'rsa_analytics_uba_winauth_newdeviceservice_score': true,
              'rsa_analytics_uba_winauth_failedserversscore_ratio': 1.9999976851892,
              'rsa_analytics_uba_winauth_highserverscore_ratio': 1.9999976851892,
              'rsa_analytics_uba_winauth_logontypescore_score': 47.45555,
              'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newdevicescore_score': 17.293288069749,
              'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_highserverscore_score': 17.293288069749,
              'rsa_analytics_uba_winauth_newserverscore_score': 76.466440348745,
              'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newserverscore_score': 8.6466440348745,
              'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_failedserversscore_score': 8.6466440348745,
              'rsa_analytics_uba_winauth_newdevice_cardinality': 2.0,
              'rsa_analytics_uba_winauth_normalized_timestamp': 1457801997000,
              'rsa_analytics_uba_winauth_normalized_hostname': 'USITBRUTSDM1',
              'rsa_analytics_uba_winauth_aggregation_aggregate': 186,
              'rsa_analytics_uba_winauth_aggregation_confidence': 66.6666666666667,
              'rsa_analytics_uba_winauth_highservers_cardinality': 2.0,
              'rsa_analytics_uba_winauth_newdevicescore_ratio': 1.9999976851892,
              'rsa_analytics_uba_winauth_newserverscore_ratio': 1.9999976851892,
              'rsa_analytics_uba_winauth_highservers_average_cardinality': 1.00000115740674,
              'rsa_analytics_uba_winauth_highserverscore_score': 66.466440348745,
              'rsa_analytics_uba_winauth_failedservers_cardinality': 2.0,
              'rsa_analytics_uba_winauth_newserver_average_cardinality': 1.00000115740674
            },
            'event_source_id': 'DEV1-IM-Concentrator.grcrtp.local:50005:29589',
            'file': '',
            'from': '',
            'ip_dst': '2.2.2.2',
            'ip_source': '192.168.1.1',
            'referer': 'curl/7.24.0',
            'related_links': [
              {
                'type': 'investigate_original_event',
                'url': ''
              },
              {
                'type': 'investigate_destination_domain',
                'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%274554mb.ru%27%2Fdate%2F2016-11-22T08%3A06%3A28.246Z%2F2016-11-22T08%3A26%3A28.246Z'
              }
            ],
            'service': 80,
            'size': 0,
            'source': {
              'device': {
                'geolocation': {}
              },
              'user': {}
            },
            'timestamp': 'Tue Oct 27 11:47:05 PDT 2015',
            'to': '',
            'type': 'Unknown',
            'user': ''
          }],
          'groupby_data_hash': '',
          'groupby_destination_country': '',
          'groupby_destination_ip': '2.2.2.2',
          'groupby_destination_port': '',
          'groupby_detector_ip': '',
          'groupby_domain': '4554mb.ru',
          'groupby_filename': '',
          'groupby_source_country': '',
          'groupby_source_ip': ['10.101.2.3'],
          'groupby_source_username': '',
          'groupby_type': '',
          'host_summary': '-,',
          'name': 'P2P software as detected by an Intrusion detection device',
          'numEvents': 1,
          'related_links': [
            {
              'type': 'investigate_session',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/'
            },
            { 'type': 'investigate_device_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/device.ip%3D%2Fdate%2F2016-11-22T08%3A06%3A28.247Z%2F2016-11-22T08%3A26%3A28.247Z'
            },
            { 'type': 'investigate_src_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.src%3D%2Fdate%2F2016-11-22T08%3A06%3A28.247Z%2F2016-11-22T08%3A26%3A28.247Z'
            },
            { 'type': 'investigate_dst_ip',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/ip.dst%3D%2Fdate%2F2016-11-22T08%3A06%3A28.247Z%2F2016-11-22T08%3A26%3A28.247Z'
            },
            { 'type': 'investigate_destination_domain',
              'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%224554mb.ru%22%2Fdate%2F2016-11-22T08%3A06%3A28.247Z%2F2016-11-22T08%3A26%3A28.247Z'
            }
          ],
          'relationships': [[
            '',
            '',
            '4554mb.ru',
            '192.168.1.1',
            '2.2.2.2',
            ''
          ]],
          'risk_score': 50,
          'severity': 50,
          'signature_id': 'Suspected C&C',
          'source': 'Event Stream Analysis',
          'timestamp': 1447799999999,
          'user_summary': [
            'MX11CL01$'
          ]
        },
        'errorMessage': null,
        'id': '5833fedca7c89226086a0955',
        'incidentCreated': 1479802590707,
        'incidentId': 'INC-134',
        'originalAlert': null,
        'originalHeaders': {
          'device_product': 'Event Stream Analysis',
          'device_vendor': 'RSA',
          'device_version': '10.4.0000',
          'name': 'P2P software as detected by an Intrusion detection device',
          'severity': 5,
          'signature_id': '2345-5432',
          'model_name': 'UBA-WinAuth',
          'timestamp': 1447799999999,
          'version': 1
        },
        'originalRawAlert': null,
        'partOfIncident': true,
        'receivedTime': 1479802588250,
        'status': 'GROUPED_IN_INCIDENT',
        'timestamp': 1447799999999
      },
      'level': 3,
      'lookup': {
        'inendebs1l2c': [[
          'ip.src',
          '192.168.1.1',
          'ip2host'
        ]]
      },
      'matched': [
        '',
        '',
        '4554mb.ru',
        '192.168.1.1',
        '2.2.2.2',
        ''
      ]
    }
  ] };
  this.set('groupedIps', ['10.10.10.10']);
  this.set('model', details.indicators);

  this.render(hbs`{{rsa-respond/incident-detail/detail-storyline i18n=i18n groupedIps=groupedIps model=model}}`);
  assert.equal(this.$('.storyline-body').length, 1, 'storyline-body exists.');
  assert.equal(this.$('.axis').length, 1, 'Axis exists.');

  assert.equal(this.$('.tree').length, 1, 'tree exists.');
  assert.equal(this.$('.date-bar').length, 2, 'only 2 date bar exists.');
  assert.equal(this.$('div.indicators').length, 4, 'indicators exists.');
  assert.equal(this.$('div.indicators .score').length, 4, 'score bump div exists');
  assert.equal(this.$('div.indicators .score-circle').length, 4, 'score bump is wrapped in circle');
  assert.equal(this.$('.indicator.non-catalyst').length, 3, 'for the given data there should be 3 non-catalysts.');
  assert.equal(this.$('.indicator-wrapper:nth-child(1) .summary').length, 1, 'storyline has summary');
  assert.equal(this.$('.indicator-wrapper:nth-child(1) .summary .indicator.non-catalyst').length, 1, 'summary has indicator object');
  assert.equal(this.$('.indicator-wrapper:nth-child(1) .summary .sub-indicator.non-catalyst').length, 7, 'summary has sub-indicator objects');
  assert.equal(this.$('.indicator-wrapper:nth-child(1) .risk-score').length, 1, 'summary has risk score');
  assert.ok(this.$('.indicator-wrapper:nth-child(1) .indicator-source div').hasClass('is-neutral'), 'content label sources rendered with right style');
  // assert.equal(this.$('.indicator-wrapper:nth-child(1) .indicator-source div').length, 2, 'C2 Packet indicator has 2 sources');

  assert.equal(this.$('.indicator-wrapper:nth-child(2) .summary').length, 1, 'storyline has summary');
  assert.equal(this.$('.indicator-wrapper:nth-child(2) .summary .indicator.non-catalyst').length, 0, 'summary has no non-catalyst indicator object');
  assert.equal(this.$('.indicator-wrapper:nth-child(2) .summary .sub-indicator.non-catalyst').length, 0, 'summary has no non-catalyst sub-indicator objects');
  assert.equal(this.$('.indicator-wrapper:nth-child(2) .summary .indicator').length, 1, 'summary has catalyst indicator object');
  assert.equal(this.$('.indicator-wrapper:nth-child(2) .summary .sub-indicator').length, 4, 'summary has catalyst sub-indicator objects');
  // assert.equal(this.$('.indicator-wrapper:nth-child(1) .indicator-source div').length, 2, 'C2 Log indicator has 2 sources');

  assert.ok(this.$('.indicator-wrapper:nth-child(3) .match-sub-indicator').length, 1, 'ECAT indicator has lookup items');
  assert.ok(this.$('.indicator-wrapper:nth-child(3) .match-sub-indicator .rsa-icon-information-circle').length, 1, 'ECAT indicator lookup items uses rsa-icon');

  assert.equal(this.$('.indicator-wrapper:nth-child(3) .indicator-source div').length, 1, 'ECAT indicator has 1 sources');
  assert.equal(this.$('.indicator-wrapper:nth-child(3) .summary .sub-indicator').length, 0, 'ECAT has no sub-indicator objects');

  assert.equal(this.$('.indicator-wrapper:nth-child(4) .match-sub-indicator').length, 0, 'UEBA Winauth indicator has no lookup items');
  assert.equal(this.$('.indicator-wrapper:nth-child(4) .indicator-source div').length, 1, 'UEBA Winauth indicator has 1 sources');
  assert.equal(this.$('.indicator-wrapper:nth-child(4) .summary .sub-indicator').length, 3, 'UEBA has 3 sub-indicator objects');
});
