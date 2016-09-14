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
        'groupby_source_ip': '',
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
        'groupby_source_ip': '',
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
        'groupby_source_ip': '',
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
        'groupby_source_ip': '',
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
