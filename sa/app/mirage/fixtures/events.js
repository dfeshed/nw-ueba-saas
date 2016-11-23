export default [
  {
    '_id': '57dcda273004ebc5ef83543d',
    'referer': 'curl/7.24.0',
    'event_source_id': 'loki-concentrator',
    'alias_host': 'google.com',
    'ip_source': '10.101.47.66',
    'directory': '',
    'ip_dst': '1.1.1.3',
    'content': 'txt',
    'detector': {
      'device_class': 'ECAT',
      'ip_address': '192.168.1.1',
      'product_name': ''
    },
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
];