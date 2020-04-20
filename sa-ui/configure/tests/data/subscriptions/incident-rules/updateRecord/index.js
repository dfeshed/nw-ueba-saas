const data = {
  'id': '59b92bbf4cb0f0092b6b6a8aasfsdf',
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
  'timeWindow': '7d',
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
  'incidentsCreatedCount': 38801
};

export default {
  subscriptionDestination: '/user/queue/alertrules/update',
  requestDestination: '/ws/respond/alertrules/update',
  message(/* frame */) {
    return {
      data
    };
  }
};