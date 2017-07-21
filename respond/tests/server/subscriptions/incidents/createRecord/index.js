const data = {
  'code': 0,
  'data': {
    'id': 'INC-24',
    'name': 'Testing 123',
    'summary': null,
    'priority': 'LOW',
    'prioritySort': 0,
    'riskScore': 0,
    'status': 'NEW',
    'statusSort': 0,
    'alertCount': 1,
    'averageAlertRiskScore': 90,
    'sealed': false,
    'totalRemediationTaskCount': 0,
    'openRemediationTaskCount': 0,
    'hasRemediationTasks': false,
    'created': 1500643843184,
    'lastUpdated': 1500643843184,
    'lastUpdatedByUser': null,
    'assignee': null,
    'sources': [
      'ECAT'
    ],
    'ruleId': null,
    'firstAlertTime': null,
    'timeWindowExpiration': null,
    'groupByValues': null,
    'categories': null,
    'notes': null,
    'createdBy': null,
    'dateIndicatorAggregationStart': null,
    'breachExportStatus': null,
    'breachData': null,
    'breachTag': null,
    'hasDeletedAlerts': false,
    'deletedAlertCount': 0,
    'groupByDomain': null,
    'enrichment': null,
    'eventCount': 1,
    'groupBySourceIp': null,
    'groupByDestinationIp': null,
    'createdFromRule': false
  },
  'request': {
    'id': 'req-3',
    'data': {
      'entity': {
        'name': 'Testing 123'
      },
      'associated': [
        {
          'id': '5833fee2a7c89226086a0956'
        }
      ]
    }
  }
};

export default {
  subscriptionDestination: '/queue/incidents/create',
  requestDestination: '/ws/respond/incidents/create',
  message() {
    return data;
  }
};