export default {
  'id': '59dcd90d6a4e19713e6c59d4',
  'order': 12,
  'enabled': false,
  'deleted': false,
  'name': 'Test',
  'description': '',
  'ruleId': 'NOT_SET',
  'uiFilterConditions': '{\"alertRuleFilterGroup\":{\"filterType\":\"FILTER_GROUP\",\"logicalOperator\":\"and\",\"filters\":[{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.type\",\"operator\":\"=\",\"value\":\"Network\"}},{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.events.domain\",\"operator\":\"=\",\"value\":\"meiske.com\"}},{\"alertRuleFilterGroup\":{\"filterType\":\"FILTER_GROUP\",\"logicalOperator\":\"or\",\"filters\":[{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.events.destination.device.port\",\"operator\":\"=\",\"value\":8080}}]}},{\"alertRuleFilterGroup\":{\"filterType\":\"FILTER_GROUP\",\"logicalOperator\":\"not\",\"filters\":[{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.severity\",\"operator\":\">=\",\"value\":80}}]}}]}}',
  'matchConditions': '{\"$and\":[{\"alert.type\":\"Network\"},{\"alert.events.domain\":\"meiske.com\"},{\"$or\":[{\"alert.events.destination.device.port\":8080}]},{\"$not\":[{\"alert.severity\":{\"$gte\":80}}]}]}',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.groupby_domain'],
  'timeWindow': { 'seconds': 3600, 'negative': false, 'zero': false, 'nano': 0, 'units': ['SECONDS', 'NANOS'] },
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': null,
    'categories': [],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': ''
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'LOW': 1, 'HIGH': 50, 'CRITICAL': 90, 'MEDIUM': 20 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0
};