export default [{
  'id': '59b92bbf4cb0f0092b6b6a8a',
  'order': 1,
  'enabled': true,
  'deleted': false,
  'name': 'Suspected Command & Control Communication By Domain',
  'description': 'This incident rule captures suspected communication with a Command & Control server and groups results by domain.',
  'ruleId': 'OOTB#1',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Event Stream Analysis"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.signature_id","operator":"=","value":"Suspected C&C"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Event Stream Analysis"},{"alert.signature_id":"Suspected C&C"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_c2domain'],
  'timeWindow': { 'seconds': 604800, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': 'Suspected C&C with ${groupByValue1}',
    'ruleSummary': 'Security Analytics detected communications with ${groupByValue1} that may be command and control malware.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review the domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigation module to locate other activity to or from it.'
  },
  'incidentScoringOptions': { 'type': 'high' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': 1508303855976,
  'alertsMatchedCount': 180872,
  'incidentsCreatedCount': 38801,
  'createdDate': 1508303855976,
  'lastUpdatedDate': 1508303855976
}, {
  'id': '59b92bbf4cb0f0092b6b6a8b',
  'order': 2,
  'enabled': false,
  'deleted': false,
  'name': 'High Risk Alerts: Malware Analysis',
  'description': 'This incident rule captures alerts generated by the RSA Malware Analysis platform as having a Risk Score of "High" or "Critical". ',
  'ruleId': 'OOTB#2',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Malware Analysis"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.risk_score","operator":">=","value":50}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Malware Analysis"},{"alert.risk_score":{"$gte":50}}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.risk_score'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a8c',
  'order': 3,
  'enabled': false,
  'deleted': false,
  'name': 'High Risk Alerts: NetWitness Endpoint',
  'description': 'This incident rule captures alerts generated by the RSA NetWitness Endpoint platform as having a Risk Score of "High" or "Critical". ',
  'ruleId': 'OOTB#3',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"ECAT"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.risk_score","operator":">=","value":50}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"ECAT"},{"alert.risk_score":{"$gte":50}}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.risk_score'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': 1508303855976,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a8d',
  'order': 4,
  'enabled': false,
  'deleted': false,
  'name': 'High Risk Alerts: Reporting Engine',
  'description': 'This incident rule captures alerts generated by the RSA Reporting Engine as having a Risk Score of "High" or "Critical". ',
  'ruleId': 'OOTB#4',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Reporting Engine"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.risk_score","operator":">=","value":50}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Reporting Engine"},{"alert.risk_score":{"$gte":50}}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.risk_score'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': 1508255285823,
  'alertsMatchedCount': 307,
  'incidentsCreatedCount': 6,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a8e',
  'order': 5,
  'enabled': false,
  'deleted': false,
  'name': 'High Risk Alerts: ESA',
  'description': 'This incident rule captures alerts generated by the RSA ESA platform as having a Risk Score of "High" or "Critical". ',
  'ruleId': 'OOTB#5',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Event Stream Analysis"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.risk_score","operator":">=","value":50}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Event Stream Analysis"},{"alert.risk_score":{"$gte":50}}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.risk_score'],
  'timeWindow': { 'seconds': 60, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': 1508254008703,
  'alertsMatchedCount': 17756,
  'incidentsCreatedCount': 9,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a8f',
  'order': 6,
  'enabled': false,
  'deleted': false,
  'name': 'IP Watch List: Activity Detected',
  'description': 'This incident rule captures alerts generated by IP addresses that have been added as "Source IP Address"   *and* "Destination IP Address" conditions of the rule.  To add additional IP addresses to the watch list, simply add a new Source and Destination IP Address conditional pair.',
  'ruleId': 'OOTB#6',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"or","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.events.source.device.ip_address","operator":"=","value":"1.1.1.1"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.events.destination.device.ip_address","operator":"=","value":"1.1.1.1"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.events.source.device.ip_address","operator":"=","value":"2.2.2.2"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.events.destination.device.ip_address","operator":"=","value":"2.2.2.2"}}] }}',
  'matchConditions': '{"$or":[{"alert.events.source.device.ip_address":"1.1.1.1"},{"alert.events.destination.device.ip_address":"1.1.1.1"},{"alert.events.source.device.ip_address":"2.2.2.2"},{"alert.events.destination.device.ip_address":"2.2.2.2"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_source_ip'],
  'timeWindow': { 'seconds': 14400, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': { 'ruleSummary': '', 'assignee': null, 'categories': [], 'ruleTitle': '${ruleName}' },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'MEDIUM': 20, 'CRITICAL': 90, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a90',
  'order': 7,
  'enabled': false,
  'deleted': false,
  'name': 'User Watch List: Activity Detected',
  'description': 'This incident rule captures alerts generated by network users whose user names have been added as a "Source UserName" condition.  To add more than one Username to the watch list, simply add an additional Source Username condition.',
  'ruleId': 'OOTB#7',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"or","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.events.source.user.username","operator":"=","value":"jsmith"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.events.source.user.username","operator":"=","value":"jdoe"}}] }}',
  'matchConditions': '{"$or":[{"alert.events.source.user.username":"jsmith"},{"alert.events.source.user.username":"jdoe"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_source_username'],
  'timeWindow': { 'seconds': 14400, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': { 'ruleSummary': '', 'assignee': null, 'categories': [], 'ruleTitle': '${ruleName}' },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'MEDIUM': 20, 'CRITICAL': 90, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a91',
  'order': 8,
  'enabled': false,
  'deleted': false,
  'name': 'Suspicious Activity Detected: Windows Worm Propagation',
  'description': 'This incident rule captures alerts that are indicative of worm propagation activity on a Microsoft network',
  'ruleId': 'OOTB#8',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Event Stream Analysis"}},{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"or","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Windows Worm Activity Detected Logs"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Windows Worm Activity Detected Packets"}}] }}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Event Stream Analysis"},{"$or":[{"alert.name":"Windows Worm Activity Detected Logs"},{"alert.name":"Windows Worm Activity Detected Packets"}] }] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_source_ip'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': { 'ruleSummary': '', 'assignee': null, 'categories': [], 'ruleTitle': '${ruleName}' },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'MEDIUM': 20, 'CRITICAL': 90, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a92',
  'order': 9,
  'enabled': false,
  'deleted': false,
  'name': 'Suspicious Activity Detected: Reconnaissance',
  'description': 'This incident rule captures alerts that identify common ICMP host identification techniques (i.e. "ping") accompanied by connection attempts to multiple service ports on a host ',
  'ruleId': 'OOTB#9',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Event Stream Analysis"}},{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"or","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Horizontal Packet"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Vertical Packet"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Horizontal Log"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Vertical Log"}}] }}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Event Stream Analysis"},{"$or":[{"alert.name":"Port Scan Horizontal Packet"},{"alert.name":"Port Scan Vertical Packet"},{"alert.name":"Port Scan Horizontal Log"},{"alert.name":"Port Scan Vertical Log"}] }] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_source_ip'],
  'timeWindow': { 'seconds': 14400, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': { 'ruleSummary': '', 'assignee': null, 'categories': [], 'ruleTitle': '${ruleName}' },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'MEDIUM': 20, 'CRITICAL': 90, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a93',
  'order': 10,
  'enabled': false,
  'deleted': false,
  'name': 'Monitoring Failure: Device Not Reporting',
  'description': 'This incident rule captures any instance of an alert designed to detect the absence of log traffic from a previously reporting device',
  'ruleId': 'OOTB#10',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Event Stream Analysis"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"No logs traffic from device in given time frame"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Event Stream Analysis"},{"alert.name":"No logs traffic from device in given time frame"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_source_ip'],
  'timeWindow': { 'seconds': 7200, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': { 'ruleSummary': '', 'assignee': null, 'categories': [], 'ruleTitle': '${ruleName}' },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'MEDIUM': 20, 'CRITICAL': 90, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6a94',
  'order': 11,
  'enabled': false,
  'deleted': false,
  'name': 'Web Threat Detection',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#11',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z11',
  'order': 12,
  'enabled': false,
  'deleted': false,
  'name': 'Suspicious Activity Detected: Reconnaissance',
  'description': 'This incident rule captures alerts that identify common ICMP host identification techniques (i.e. "ping") accompanied by connection attempts to multiple service ports on a host ',
  'ruleId': 'OOTB#9',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Event Stream Analysis"}},{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"or","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Horizontal Packet"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Vertical Packet"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Horizontal Log"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"Port Scan Vertical Log"}}] }}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Event Stream Analysis"},{"$or":[{"alert.name":"Port Scan Horizontal Packet"},{"alert.name":"Port Scan Vertical Packet"},{"alert.name":"Port Scan Horizontal Log"},{"alert.name":"Port Scan Vertical Log"}] }] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_source_ip'],
  'timeWindow': { 'seconds': 14400, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': { 'ruleSummary': '', 'assignee': null, 'categories': [], 'ruleTitle': '${ruleName}' },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'MEDIUM': 20, 'CRITICAL': 90, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z12',
  'order': 13,
  'enabled': false,
  'deleted': false,
  'name': 'Monitoring Failure: Device Not Reporting',
  'description': 'This incident rule captures any instance of an alert designed to detect the absence of log traffic from a previously reporting device',
  'ruleId': 'OOTB#10',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Event Stream Analysis"}},{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"No logs traffic from device in given time frame"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Event Stream Analysis"},{"alert.name":"No logs traffic from device in given time frame"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_source_ip'],
  'timeWindow': { 'seconds': 7200, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': { 'ruleSummary': '', 'assignee': null, 'categories': [], 'ruleTitle': '${ruleName}' },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'MEDIUM': 20, 'CRITICAL': 90, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z13',
  'order': 14,
  'enabled': false,
  'deleted': false,
  'name': 'Web Threat Detection',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#11',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z14',
  'order': 15,
  'enabled': false,
  'deleted': false,
  'name': 'Web Threat Detection',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#12',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z15',
  'order': 16,
  'enabled': false,
  'deleted': false,
  'name': 'Huh Threat Detection',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#13',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z16',
  'order': 17,
  'enabled': false,
  'deleted': false,
  'name': 'Foo Threat Detection',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#14',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z17',
  'order': 18,
  'enabled': false,
  'deleted': false,
  'name': 'Zoo Threat Detection',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#15',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z18',
  'order': 19,
  'enabled': false,
  'deleted': false,
  'name': 'Wat Rule',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#16',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}, {
  'id': '59b92bbf4cb0f0092b6b6z19',
  'order': 20,
  'enabled': false,
  'deleted': false,
  'name': 'Moar Web Threat Detection',
  'description': 'This incident rule captures alerts generated by the RSA Web Threat Detection platform. ',
  'ruleId': 'OOTB#17',
  'uiFilterConditions': '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.source","operator":"=","value":"Web Threat Detection"}}] }}',
  'matchConditions': '{"$and":[{"alert.source":"Web Threat Detection"}] }',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.signature_id'],
  'timeWindow': { 'seconds': 3600, 'nano': 0, 'negative': false, 'zero': false, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'CRITICAL': 90, 'MEDIUM': 20, 'HIGH': 50, 'LOW': 1 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0,
  'createdDate': null,
  'lastUpdatedDate': null
}];
