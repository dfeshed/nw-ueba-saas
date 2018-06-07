export default {
  ALERT: {
    fields: [
      'timestamp',
      'risk_score',
      'source',
      'numEvents',
      'summary',
      'incident'
    ]
  },
  INCIDENT: {
    fields: [
      'created',
      'priority',
      'averageAlertRiskScore',
      '_id',
      'status',
      'assignee',
      'alertCount'
    ]
  }
};