const now = new Date();
export default [
  {
    indicator: {
      id: 'id1',
      timestamp: now,
      originalHeaders: {
        'device_product': 'Event Stream Analysis',
        'model_name': 'C2-Log',
        name: 'Indicator1'
      },
      alert: {
        severity: 50,
        relationships: [
          [
            '',
            '',
            'g00gle.com',
            '76.64.188.48',
            '3.3.3.3',
            ''
          ]
        ],
        events: [
          {
            timestamp: now,
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
            }
          }
        ]
      }
    },
    group: '0',
    matched: []
  }]
;