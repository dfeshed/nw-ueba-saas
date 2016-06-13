import Ember from 'ember';
/**
 * @file MockServer message handlers that respond to requests regarding incident model(s).
 * Here we can register handlers for requests related to incidents, such as streaming a list of incidents, fetching
 * a single incident record by id, or updating incidents.
 * @public
 */
let _alertsMap = null;

function _makeAlertsMap(dbAlerts) {
  if (dbAlerts && !_alertsMap) {
    _alertsMap = {};
    dbAlerts.forEach(function(alert) {
      let incId = alert.incidentId;
      if (incId) {
        let arr = _alertsMap[incId];
        if (!arr) {
          arr = _alertsMap[incId] = [];
        }
        arr.push(alert);
      }
    });
  }
  return _alertsMap;
}

export default function(server) {
  server.route('incident', 'stream', function(message, frames, server) {
    // Wait until after all incidents DB has been loaded from a JSON file.

    server.asyncFixturesPromise.then(() => {
      let { filter } = frames[0].body,
        statusFilter = (filter || []).findBy('field', 'statusSort') || {},
        records = server.mirageServer.db.incident,
        filteredRecords = [];

      if (statusFilter && Ember.typeOf(statusFilter.value) !== 'undefined') {
        filteredRecords = records.where({ statusSort: statusFilter.value });
      } else if (statusFilter && Ember.typeOf(statusFilter.values) !== 'undefined') {
        statusFilter.values.forEach((filter) => {
          filteredRecords.pushObjects(records.where({ statusSort: filter }));
        });
      } else {
        filteredRecords = records;
      }

      let map = _makeAlertsMap(server.mirageServer.db.alerts);

      filteredRecords = filteredRecords.map((incident) => {
        incident.riskScore = Math.min(99, (10 + Math.round(100 * Math.random())));
        if (map && !incident.alerts) {
          incident.alerts = !map[incident.id] ? [] : map[incident.id].map((alert) => {
            return { alert: alert.alert };
          });
        }
        return incident;
      });

      server.streamList(
        filteredRecords,
        frames[0].body.page,
        null,
        frames);
    });
  });

  server.route('incident', 'notify', function(message, frames, server) {
    // Wait until after all incidents DB has been loaded from a JSON file.

    server.asyncFixturesPromise.then(() => {
      let newIncident = { '_class': 'com.rsa.smc.im.domain.bean.Incident',
        'id': 'INC-000', 'alertCount': 1,
        'assignee': { 'emailAddress': 'admin@rsa.com', 'name': 'Administrator', 'id': '1', 'login': 'admin' },
        'averageAlertRiskScore': 50,
        'breachExportStatus': 'NONE',
        'categories': [],
        'created': 1452485774539,
        'createdBy': 'Suspected Command & Control Communication By Domain',
        'deletedAlertCount': 0,
        'firstAlertTime': 1452485729000 ,
        'groupByValues': [ 'www.media.gwu.edu' ],
        'hasDeletedAlerts': false,
        'hasRemediationTasks': false,
        'lastUpdated': 1452485774539 ,
        'name': 'Suspected command and control communication with www.media.gwu.edu',
        'openRemediationTaskCount': 0,
        'priority': 'HIGH',
        'prioritySort': 2,
        'riskScore': 50,
        'ruleId': '5681b379e4b0947bc54e6c9d',
        'sealed': false,
        'sources': [ 'Event Stream Analysis' ], 'status': 'NEW', 'statusSort': 0,
        'statushistory': [ { 'date_changed': 1452485774542, 'status': 'NEW' } ],
        'summary': 'SA detected communications with www.media.gwu.edu that may be malware command and control.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigations module to locate other activity to/from it.',
        'timeWindowExpiration': 1453090529000 ,
        'totalRemediationTaskCount': 0,
        alerts: {
          alert: [
            {
              '_class': 'com.rsa.smc.im.domain.bean.Alert',
              'id': '56932c61e4b0e179f1af6556',
              'alert': {
                'destination_country': ['United States'],
                'groupby_type': 'Network',
                'user_summary': [''],
                'groupby_domain': 'www.media.gwu.edu',
                'source': 'Event Stream Analysis',
                'type': ['Network'],
                'groupby_source_country': 'United States',
                'groupby_destination_country': 'United States',
                'signature_id': 'Suspected C&C',
                'groupby_filename': 'cotlow_awards.cfm',
                'groupby_data_hash': '',
                'groupby_destination_ip': '161.253.149.52',
                'groupby_source_ip': '66.249.67.67',
                'groupby_source_username': '',
                'groupby_detector_ip': '',
                'events': [{
                  'related_links': [{
                    'type': 'investigate_original_event',
                    'url': '/investigation/host/10.101.217.121:56005/navigate/event/AUTO/21053778'
                  }, {
                    'type': 'investigate_destination_domain',
                    'url': "/investigation/10.101.217.121:56005/navigate/query/alias.host%3D'www.media.gwu.edu'%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A14%3A22.000Z"
                  }],
                  'data': [{
                    'filename': 'cotlow_awards.cfm',
                    'size': 23704
                  }],
                  'destination': {
                    'device': {
                      'compliance_rating': '',
                      'netbios_name': '',
                      'port': 80,
                      'mac_address': '00:13:c3:3b:be:00',
                      'criticality': '',
                      'asset_type': '',
                      'ip_address': '161.253.149.52',
                      'facility': '',
                      'business_unit': '',
                      'geolocation': {
                        'country': 'United States',
                        'city': 'Washington',
                        'latitude': 38.93759918212891,
                        'organization': 'The George Washington University',
                        'domain': 'gwu.edu',
                        'longitude': -77.09279632568359
                      }
                    },
                    'user': {
                      'email_address': '',
                      'ad_username': '',
                      'ad_domain': '',
                      'username': ''
                    }
                  },
                  'description': '',
                  'source': {
                    'device': {
                      'compliance_rating': '',
                      'netbios_name': '',
                      'port': 35444,
                      'mac_address': '00:13:c3:3b:c7:00',
                      'criticality': '',
                      'asset_type': '',
                      'ip_address': '66.249.67.67',
                      'facility': '',
                      'business_unit': '',
                      'geolocation': {
                        'country': 'United States',
                        'city': 'Mountain View',
                        'latitude': 37.4192008972168,
                        'organization': 'Googlebot',
                        'domain': 'googlebot.com',
                        'longitude': -122.0574035644531
                      }
                    },
                    'user': {
                      'email_address': '',
                      'ad_username': '',
                      'ad_domain': '',
                      'username': ''
                    }
                  },
                  'type': 'Network',
                  'enrichment': {
                    'command_control': {
                      'weighted_domain_ua_ratio_score': 4,
                      'weighted_domain_referer_ratio_score': 4,
                      'weighted_domain_referer_score': 2,
                      'confidence': 40,
                      'weighted_whois_age_score': 0,
                      'weighted_whois_validity_score': 0,
                      'aggregate': 100
                    },
                    'ctxhub': {
                      'domain_is_whitelisted': false
                    },
                    'whois': {
                      'estimated_domain_validity_days': 2601,
                      'expires_date': '19-jan-2023',
                      'registrant_country': 'US',
                      'registrar_name': 'MARKMONITOR INC.',
                      'is_cached': true,
                      'registrant_email': 'domainadmin@yahoo-inc.com',
                      'source': 'DATABASE',
                      'age_score': 0,
                      'scaled_validity': 10,
                      'domain_name': 'yahoo.com',
                      'scaled_age': 10,
                      'registrant_state': 'CA',
                      'estimated_domain_age_days': 4741,
                      'registrant_name': 'Domain Administrator',
                      'registrant_organization': 'Yahoo! Inc.',
                      'registrant_postal_code': '94089',
                      'registrant_street1': '701 First Avenue',
                      'registrant_telephone': '1.4083493300',
                      'created_date': '18-jan-1995',
                      'updated_date': '06-sep-2013',
                      'registrant_city': 'Sunnyvale',
                      'validity_score': 0
                    },
                    'normalized': {
                      'full_domain': 'www.media.gwu.edu',
                      'domain': 'gwu.edu',
                      'srcip_domain': '66.249.67.67_gwu.edu',
                      'user_agent': 'Mozilla/5.0',
                      'timestamp': 1452485662000
                    },
                    'domain': {
                      'ua_ratio_score': 100,
                      'referer_num_events': 191,
                      'ua_cardinality': 1,
                      'referer_ratio': 100,
                      'referer_cardinality': 1,
                      'referer_conditional_cardinality': 1,
                      'ua_num_events': 191,
                      'ua_score': 100,
                      'referer_ratio_score': 100,
                      'referer_score': 100,
                      'ua_conditional_cardinality': 1,
                      'ua_ratio': 100
                    },
                    'beaconing': {
                      'beaconing_score': 89.35304630354096,
                      'beaconing_period': 60950
                    },
                    'new_domain': {
                      'age_num_events': 28,
                      'age_age': 528000,
                      'age_score': 99.39075237491708
                    },
                    'httpEventEnrichedRule': {
                      'flow_name': 'C2'
                    },
                    'user_agent': {
                      'rare_num_events': 30,
                      'rare_score': 27.25317930340126,
                      'rare_cardinality': 27
                    },
                    'smooth': {
                      'smooth_beaconing_score': 97.42054727927368
                    }
                  },
                  'file': 'cotlow_awards.cfm',
                  'size': 23704,
                  'detected_by': '',
                  'domain': 'www.media.gwu.edu',
                  'from': '66.249.67.67:35444',
                  'to': '161.253.149.52:80',
                  'detector': {
                    'device_class': '',
                    'ip_address': '',
                    'product_name': ''
                  },
                  'user': '',
                  'timestamp': '2016-01-11T04:14:22.000Z'
                }],
                'timestamp': 1452485729000,
                'severity': 50,
                'related_links': [{
                  'type': 'investigate_session',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/sessionid%3D21053778'
                }, {
                  'type': 'investigate_src_ip',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/ip.src%3D66.249.67.67%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z'
                }, {
                  'type': 'investigate_dst_ip',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/ip.dst%3D161.253.149.52%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z'
                }, {
                  'type': 'investigate_destination_domain',
                  'url': "/investigation/10.101.217.121:56005/navigate/query/alias.host%3D'www.media.gwu.edu'%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z"
                }],
                'host_summary': '66.249.67.67:35444 to 161.253.149.52:80',
                'risk_score': 20,
                'groupby_destination_port': '80',
                'source_country': ['United States'],
                'name': 'Suspected C&C',
                'numEvents': 1
              },
              'incidentCreated': 1452485774539,
              'incidentId': 'INC-000',
              'originalAlert': {
                'instance_id': 'ca26c066fc99b015f8f4401f2908939f',
                'engineUri': 'flow',
                'events': [{
                  'ip_proto': 6,
                  'extension': 'cfm',
                  'alias_host': ['www.media.gwu.edu'],
                  'ip_src': '66.249.67.67',
                  'lifetime': 39,
                  'domain_src': 'googlebot.com',
                  'medium': 1,
                  'sessionid': 21053778,
                  'rid': 21053777,
                  'directory': '/~anth/atgw/',
                  'content': 'text/html',
                  'packets': 60,
                  'eth_src': '00:13:c3:3b:c7:00',
                  'enrichment': {
                    'command_control': {
                      'weighted_domain_ua_ratio_score': 4,
                      'weighted_domain_referer_ratio_score': 4,
                      'weighted_domain_referer_score': 2,
                      'confidence': 40,
                      'weighted_whois_age_score': 0,
                      'weighted_whois_validity_score': 0,
                      'aggregate': 100
                    },
                    'ctxhub': {
                      'domain_is_whitelisted': false
                    },
                    'whois': {
                      'estimated_domain_validity_days': 2601,
                      'expires_date': '19-jan-2023',
                      'registrant_country': 'US',
                      'registrar_name': 'MARKMONITOR INC.',
                      'is_cached': true,
                      'registrant_email': 'domainadmin@yahoo-inc.com',
                      'source': 'DATABASE',
                      'age_score': 0,
                      'scaled_validity': 10,
                      'domain_name': 'yahoo.com',
                      'scaled_age': 10,
                      'registrant_state': 'CA',
                      'estimated_domain_age_days': 4741,
                      'registrant_name': 'Domain Administrator',
                      'registrant_organization': 'Yahoo! Inc.',
                      'registrant_postal_code': '94089',
                      'registrant_street1': '701 First Avenue',
                      'registrant_telephone': '1.4083493300',
                      'created_date': '18-jan-1995',
                      'updated_date': '06-sep-2013',
                      'registrant_city': 'Sunnyvale',
                      'validity_score': 0
                    },
                    'normalized': {
                      'full_domain': 'www.media.gwu.edu',
                      'domain': 'gwu.edu',
                      'srcip_domain': '66.249.67.67_gwu.edu',
                      'user_agent': 'Mozilla/5.0',
                      'timestamp': 1452485662000
                    },
                    'domain': {
                      'ua_ratio_score': 100,
                      'referer_num_events': 191,
                      'ua_cardinality': 1,
                      'referer_ratio': 100,
                      'referer_cardinality': 1,
                      'referer_conditional_cardinality': 1,
                      'ua_num_events': 191,
                      'ua_score': 100,
                      'referer_ratio_score': 100,
                      'referer_score': 100,
                      'ua_conditional_cardinality': 1,
                      'ua_ratio': 100
                    },
                    'beaconing': {
                      'beaconing_score': 89.35304630354096,
                      'beaconing_period': 60950
                    },
                    'new_domain': {
                      'age_num_events': 28,
                      'age_age': 528000,
                      'age_score': 99.39075237491708
                    },
                    'httpEventEnrichedRule': {
                      'flow_name': 'C2'
                    },
                    'user_agent': {
                      'rare_num_events': 30,
                      'rare_score': 27.25317930340126,
                      'rare_cardinality': 27
                    },
                    'smooth': {
                      'smooth_beaconing_score': 97.42054727927368
                    }
                  },
                  'rpackets': 17,
                  'latdec_dst': 38.93759918212891,
                  'payload': 19928,
                  'tcp_flags': 27,
                  'longdec_src': -122.0574035644531,
                  'action': ['get'],
                  'client': 'Mozilla/5.0',
                  'alias_ip': ['161.253.149.52'],
                  'city_src': 'Mountain View',
                  'country_dst': 'United States',
                  'org_dst': 'The George Washington University',
                  'event_source_id': '10.101.217.121:56005:21053778',
                  'esa_time': 1452485729299,
                  'tcp_dstport': 80,
                  'tcp_srcport': 35444,
                  'query': 'year=737',
                  'streams': 2,
                  'domain_dst': 'gwu.edu',
                  'ip_dst': '161.253.149.52',
                  'longdec_dst': -77.09279632568359,
                  'eth_dst': '00:13:c3:3b:be:00',
                  'eth_type': 2048,
                  'latdec_src': 37.4192008972168,
                  'filename': 'cotlow_awards.cfm',
                  'size': 23704,
                  'service': 80,
                  'country_src': 'United States',
                  'city_dst': 'Washington',
                  'time': 1452485662,
                  'org_src': 'Googlebot',
                  'rpayload': 14946,
                  'did': 'pd'
                }]
              },
              'originalHeaders': {
                'severity': 5,
                'device_version': '10.6.0.0.1298.g6ca3d05.1',
                'x-received-from': [{
                  'exchange': {
                    'bytes': {
                      '$binary': 'Y2FybG9zLmFsZXJ0cw==',
                      '$type': '00'
                    },
                    '_class': 'com.rabbitmq.client.impl.LongStringHelper$ByteArrayLongString'
                  },
                  'uri': {
                    'bytes': {
                      '$binary': 'YW1xcHM6Ly8xMC4xMDEuMjE3LjE0NQ==',
                      '$type': '00'
                    },
                    '_class': 'com.rabbitmq.client.impl.LongStringHelper$ByteArrayLongString'
                  },
                  'redelivered': false,
                  'cluster-name': {
                    'bytes': {
                      '$binary': 'c2FAZXNh',
                      '$type': '00'
                    },
                    '_class': 'com.rabbitmq.client.impl.LongStringHelper$ByteArrayLongString'
                  }
                }],
                'device_product': 'Event Stream Analysis',
                'signature_id': 'Suspected C&C',
                'timestampAsLong': 1452485729000,
                'name': 'Suspected C&C',
                'device_vendor': 'RSA',
                'version': 0,
                'timestamp': 1452485729000
              },
              'originalRawAlert': '{\'events\': [{\'action\':[\'get\'], \'alias_host\':[\'www.media.gwu.edu\'], \'alias_ip\':[\'161.253.149.52\'], \'city_dst\':\'Washington\', \'city_src\':\'Mountain View\', \'client\':\'Mozilla/5.0\', \'content\':\'text/html\', \'country_dst\':\'United States\', \'country_src\':\'United States\', \'did\':\'pd\', \'directory\':\'/~anth/atgw/\', \'domain_dst\':\'gwu.edu\', \'domain_src\':\'googlebot.com\', \'enrichment\':{\'beaconing\':{\'beaconing_period\':60950, \'beaconing_score\':89.35304630354096}, \'command_control\':{\'aggregate\':100, \'confidence\':40, \'weighted_domain_referer_ratio_score\':4, \'weighted_domain_referer_score\':2, \'weighted_domain_ua_ratio_score\':4, \'weighted_whois_age_score\':0, \'weighted_whois_validity_score\':0}, \'ctxhub\':{\'domain_is_whitelisted\':false}, \'domain\':{\'referer_cardinality\':1, \'referer_conditional_cardinality\':1, \'referer_num_events\':191, \'referer_ratio\':100, \'referer_ratio_score\':100, \'referer_score\':100, \'ua_cardinality\':1, \'ua_conditional_cardinality\':1, \'ua_num_events\':191, \'ua_ratio\':100, \'ua_ratio_score\':100, \'ua_score\':100}, \'httpEventEnrichedRule\':{\'flow_name\':\'C2\'}, \'new_domain\':{\'age_age\':528000, \'age_num_events\':28, \'age_score\':99.39075237491708}, \'normalized\':{\'domain\':\'gwu.edu\', \'full_domain\':\'www.media.gwu.edu\', \'srcip_domain\':\'66.249.67.67_gwu.edu\', \'timestamp\':1452485662000, \'user_agent\':\'Mozilla/5.0\'}, \'smooth\':{\'smooth_beaconing_score\':97.42054727927368}, \'user_agent\':{\'rare_cardinality\':27, \'rare_num_events\':30, \'rare_score\':27.25317930340126}}, \'esa_time\':1452485729299, \'eth_dst\':\'00:13:c3:3b:be:00\', \'eth_src\':\'00:13:c3:3b:c7:00\', \'eth_type\':2048, \'event_source_id\':\'10.101.217.121:56005:21053778\', \'extension\':\'cfm\', \'filename\':\'cotlow_awards.cfm\', \'ip_dst\':\'161.253.149.52\', \'ip_proto\':6, \'ip_src\':\'66.249.67.67\', \'latdec_dst\':38.937599182128906, \'latdec_src\':37.4192008972168, \'lifetime\':39, \'longdec_dst\':-77.0927963256836, \'longdec_src\':-122.05740356445312, \'medium\':1, \'org_dst\':\'The George Washington University\', \'org_src\':\'Googlebot\', \'packets\':60, \'payload\':19928, \'query\':\'year=737\', \'rid\':21053777, \'rpackets\':17, \'rpayload\':14946, \'service\':80, \'sessionid\':21053778, \'size\':23704, \'streams\':2, \'tcp_dstport\':80, \'tcp_flags\':27, \'tcp_srcport\':35444, \'time\':1452485662}], \'engineUri\': \'flow\', \'instance_id\': \'ca26c066fc99b015f8f4401f2908939f\'}\n',
              'partOfIncident': true,
              'receivedTime': 1452485729741,
              'status': 'GROUPED_IN_INCIDENT'
            }
          ]
        }
      };

      let response = [],
        incident = server.mirageServer.db.incident.where({ 'statusSort': 0 });
      incident = incident.toArray();
      // update the first 10 incidents
      let someIncidents = incident.slice(0, 10);
      someIncidents.forEach((incident) => {
        incident.riskScore = Math.min(99, (10 + Math.round(100 * Math.random())));
        response.push(incident);
      });

      // create a new incident
      response.push(newIncident);

      let assignedIncident = { '_class': 'com.rsa.smc.im.domain.bean.Incident',
        'id': 'INC-001', 'alertCount': 1,
        'assignee': { 'emailAddress': 'admin@rsa.com', 'name': 'Administrator', 'id': '1', 'login': 'admin' },
        'averageAlertRiskScore': 50,
        'breachExportStatus': 'NONE',
        'categories': [],
        'created': 1452485774539,
        'createdBy': 'Suspected Command & Control Communication By Domain',
        'deletedAlertCount': 0,
        'firstAlertTime': 1452485729000 ,
        'groupByValues': [ 'www.media.gwu.edu' ],
        'hasDeletedAlerts': false,
        'hasRemediationTasks': false,
        'lastUpdated': 1452485774539 ,
        'name': 'Suspected command and control communication with www.media.gwu.edu',
        'openRemediationTaskCount': 0,
        'priority': 'HIGH',
        'prioritySort': 2,
        'riskScore': 50,
        'ruleId': '5681b379e4b0947bc54e6c9d',
        'sealed': false,
        'sources': [ 'Event Stream Analysis' ], 'status': 'ASSIGNED', 'statusSort': 1,
        'statushistory': [ { 'date_changed': 1452485774542, 'status': 'NEW' } ],
        'summary': 'SA detected communications with www.media.gwu.edu that may be malware command and control.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigations module to locate other activity to/from it.',
        'timeWindowExpiration': 1453090529000 ,
        'totalRemediationTaskCount': 0,
        alerts: {
          alert: [
            {
              '_class': 'com.rsa.smc.im.domain.bean.Alert',
              'id': '56932c61e4b0e179f1af6556',
              'alert': {
                'timestamp': 1452485729000,
                'severity': 50,
                'related_links': [{
                  'type': 'investigate_session',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/sessionid%3D21053778'
                }, {
                  'type': 'investigate_src_ip',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/ip.src%3D66.249.67.67%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z'
                }, {
                  'type': 'investigate_dst_ip',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/ip.dst%3D161.253.149.52%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z'
                }, {
                  'type': 'investigate_destination_domain',
                  'url': "/investigation/10.101.217.121:56005/navigate/query/alias.host%3D'www.media.gwu.edu'%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z"
                }],
                'host_summary': '66.249.67.67:35444 to 161.253.149.52:80',
                'risk_score': 20,
                'groupby_destination_port': '80',
                'source_country': ['United States'],
                'name': 'Suspected C&C',
                'numEvents': 1
              },
              'incidentCreated': 1452485774539,
              'incidentId': 'INC-001'
            }
          ]
        }
      };

      response.push(assignedIncident);

      let inProgressIncident = { '_class': 'com.rsa.smc.im.domain.bean.Incident',
        'id': 'INC-002', 'alertCount': 1,
        'assignee': { 'emailAddress': 'admin@rsa.com', 'name': 'Administrator', 'id': '1', 'login': 'admin' },
        'averageAlertRiskScore': 50,
        'breachExportStatus': 'NONE',
        'categories': [],
        'created': 1452485774539,
        'createdBy': 'Suspected Command & Control Communication By Domain',
        'deletedAlertCount': 0,
        'firstAlertTime': 1452485729000 ,
        'groupByValues': [ 'www.media.gwu.edu' ],
        'hasDeletedAlerts': false,
        'hasRemediationTasks': false,
        'lastUpdated': 1452485774539 ,
        'name': 'Suspected command and control communication with www.media.gwu.edu',
        'openRemediationTaskCount': 0,
        'priority': 'HIGH',
        'prioritySort': 2,
        'riskScore': 50,
        'ruleId': '5681b379e4b0947bc54e6c9d',
        'sealed': false,
        'sources': [ 'Event Stream Analysis' ], 'status': 'IN PROGRESS', 'statusSort': 2,
        'statushistory': [ { 'date_changed': 1452485774542, 'status': 'NEW' } ],
        'summary': 'SA detected communications with www.media.gwu.edu that may be malware command and control.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigations module to locate other activity to/from it.',
        'timeWindowExpiration': 1453090529000 ,
        'totalRemediationTaskCount': 0,
        alerts: {
          alert: [
            {
              '_class': 'com.rsa.smc.im.domain.bean.Alert',
              'id': '56932c61e4b0e179f1af6556',
              'alert': {
                'timestamp': 1452485729000,
                'severity': 50,
                'related_links': [{
                  'type': 'investigate_session',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/sessionid%3D21053778'
                }, {
                  'type': 'investigate_src_ip',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/ip.src%3D66.249.67.67%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z'
                }, {
                  'type': 'investigate_dst_ip',
                  'url': '/investigation/10.101.217.121:56005/navigate/query/ip.dst%3D161.253.149.52%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z'
                }, {
                  'type': 'investigate_destination_domain',
                  'url': "/investigation/10.101.217.121:56005/navigate/query/alias.host%3D'www.media.gwu.edu'%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z"
                }],
                'host_summary': '66.249.67.67:35444 to 161.253.149.52:80',
                'risk_score': 20,
                'groupby_destination_port': '80',
                'source_country': ['United States'],
                'name': 'Suspected C&C',
                'numEvents': 1
              },
              'incidentCreated': 1452485774539,
              'incidentId': 'INC-001'
            }
          ]
        }
      };

      response.push(inProgressIncident);

      server.streamList(
        response,
        frames[0].body.page,
        null,
        frames);
    });
  });

  server.route('incident', 'findRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {},
      incident = server.mirageServer.db.incident.find(frame.body.id),
      map = _makeAlertsMap(server.mirageServer.db.alerts);

    if (map) {
      incident.alerts = !map[incident.id] ? [] : map[incident.id].map((alert) => {
        let json = {};
        json.alert = alert.alert;
        return json;
      });
    }
    incident.riskScore = Math.min(99, (10 + Math.round(100 * Math.random())));
    incident.prioritySort = Math.round(3 * Math.random());
    incident.id = frame.body.id;
    incident.type = 'incident';

    server.sendFrame('MESSAGE', {
        subscription: (frame.headers || {}).id || '',
        'content-type': 'application/json'
      },
      {
        code: 0,
        data: incident,
        request: frame.body
      });
  });

  server.route('incident', 'updateRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {},
      incident = server.mirageServer.db.incident.update(frame.body.id, frame.body);

    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: incident,
      request: frame.body
    });
  });
}
