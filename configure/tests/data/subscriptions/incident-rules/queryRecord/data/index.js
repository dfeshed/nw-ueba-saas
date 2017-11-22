export default {
  'id': '59dcd90d6a4e19713e6c59d4',
  'order': 12,
  'enabled': true,
  'deleted': false,
  'name': 'Thou shalt not break this rule',
  'description': 'Any fool can make a rule. And any fool will mind it.',
  'ruleId': 'NOT_SET',
  'uiFilterConditions': '{\"alertRuleFilterGroup\":{\"filterType\":\"FILTER_GROUP\",\"logicalOperator\":\"and\",\"filters\":[{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.type\",\"operator\":\"=\",\"value\":\"Network\"}},{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.events.domain\",\"operator\":\"=\",\"value\":\"meiske.com\"}},{\"alertRuleFilterGroup\":{\"filterType\":\"FILTER_GROUP\",\"logicalOperator\":\"or\",\"filters\":[{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.events.destination.device.port\",\"operator\":\"=\",\"value\":8080}}]}},{\"alertRuleFilterGroup\":{\"filterType\":\"FILTER_GROUP\",\"logicalOperator\":\"not\",\"filters\":[{\"alertRuleFilter\":{\"filterType\":\"FILTER\",\"property\":\"alert.severity\",\"operator\":\">=\",\"value\":80}}]}}]}}',
  'matchConditions': '{\"$and\":[{\"alert.type\":\"Network\"},{\"alert.events.domain\":\"meiske.com\"},{\"$or\":[{\"alert.events.destination.device.port\":8080}]},{\"$not\":[{\"alert.severity\":{\"$gte\":80}}]}]}',
  'advancedUiFilterConditions': false,
  'groupByFields': ['alert.name'],
  'timeWindow': '7d',
  'action': 'GROUP_INTO_INCIDENT',
  'incidentCreationOptions': {
    'assignee': {
      'id': '3',
      'name': 'Stanley Nellie',
      'email': null,
      'description': 'person2@test.com',
      'type': null,
      'accountId': null,
      'disabled': false
    },
    'categories': [{
      'id': '58c690184d5aff1637200187',
      'parent': 'Environmental',
      'name': 'Deterioration'
    }],
    'ruleTitle': '${ruleName} for ${groupByValue1}',
    'ruleSummary': 'Summary'
  },
  'incidentScoringOptions': { 'type': 'average' },
  'priorityScale': { 'LOW': 5, 'HIGH': 55, 'CRITICAL': 95, 'MEDIUM': 25 },
  'notificationOptions': {},
  'lastMatched': null,
  'alertsMatchedCount': 0,
  'incidentsCreatedCount': 0
};