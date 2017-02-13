const data = {
  'id': 'INC-18',
  'name': 'Suspected C&C with g00gle.com',
  'summary': 'Security Analytics detected communications with g00gle.com that may be command and control malware. 1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.). 2. Review the domain registration for suspect information (Registrant country, registrar, no registration data found, etc). 3. If the domain is suspect, go to the Investigation module to locate other activity to or from it.',
  'priority': 'CRITICAL',
  'prioritySort': 3,
  'riskScore': 90,
  'status': 'NEW',
  'statusSort': 0,
  'alertCount': 1,
  'averageAlertRiskScore': 90,
  'sealed': true,
  'totalRemediationTaskCount': 0,
  'openRemediationTaskCount': 0,
  'hasRemediationTasks': false,
  'created': 1483657112176,
  'lastUpdated': 1483657112176,
  'lastUpdatedByUser': null,
  'assignee': null,
  'sources': [
    'Event Stream Analysis'
  ],
  'ruleId': '586c17a5ecd2590853bebef5',
  'firstAlertTime': 1483610607482,
  'timeWindowExpiration': 1484215407482,
  'groupByValues': [
    'g00gle.com'
  ],
  'categories': [
    'Malware', 'Command & Control'
  ],
  notes: [
    {
      author: 'Tony',
      notes: 'Assigned to you. Please take a look.',
      created: 1483990366970
    },
    {
      author: 'Ian',
      notes: 'Will do. Still wrapping up the firewall issue (INC-105). Probably wont get to this til tomorrow.',
      created: 1483993366970
    },
    {
      author: 'Tony',
      notes: 'FYI: This incident might be related to INC-121, 128, 136 & 151.  Same user. Escalating the priority.',
      created: 1483996366970
    },
    {
      author: 'Ian',
      notes: 'Deferring the firewall issue. Starting on this one now.',
      created: 1483999366970
    }
  ],
  'createdBy': 'Suspected Command & Control Communication By Domain',
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
  'catalystRelationships': [

  ],
  'relatedIndicators': {

  },
  'relationshipFieldSources': null,
  'dateLastRelationship': 1483657112177,
  'dateIndicatorAggregationStart': 1482746607482,
  'breachExportStatus': 'NONE',
  'breachData': null,
  'breachTag': null,
  'hasDeletedAlerts': false,
  'deletedAlertCount': 0,
  'groupByDomain': [
    'g00gle.com'
  ],
  'enrichment': [
    {
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
      }
    }
  ],
  features: [{
    title: 'Rare Domain',
    score: 87
  }, {
    title: 'Domain Age',
    score: 75
  }, {
    title: 'Rare User Agent',
    score: 51
  }, {
    title: 'No Referrers',
    score: 51
  }, {
    title: 'Beacon Behavior',
    score: 75
  }, {
    title: 'C2 Risk',
    score: 90
  }],
  'eventCount': 1,
  'groupBySourceIp': [
    '10.64.188.48'
  ],
  'groupByDestinationIp': [
    '3.3.3.3'
  ],
  'createdFromRule': true,
  domainRegistration: {
    date: 1483999366970,
    registrar: 'Melbourne IT, LTD',
    name: 'Symantic Corp',
    address: '350 Ellis Street, Mountain View, CA'
  }
};

export default {
  subscriptionDestination: '/user/queue/incident/details',
  requestDestination: '/ws/response/incident/details',
  message(/* frame */) {
    return {
      data
    };
  }
};