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
      'type': ['User Entity']
    }
  }
];
