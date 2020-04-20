export default {
  id: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
  name: 'non_standard_hours',
  startDate: 1543183200000,
  endDate: 1543186800000,
  entityType: 'User',
  entityName: 'critical',
  entityId: 'af2284e2-d278-4101-97aa-7a980e781bf0',
  evidences: [
    {
      id: '8614aa7f-c8ee-4824-9eaf-e0bb199cd006',
      entityType: 'User',
      entityTypeFieldName: null,
      entityName: 'critical',
      startDate: 1543183200000,
      endDate: 1543183200000,
      retentionDate: null,
      anomalyType: null,
      anomalyTypeFieldName: 'abnormal_event_day_time',
      name: 'abnormal_event_day_time',
      anomalyValue: '2018-11-25T22:00:00Z',
      dataEntitiesIds: ['file'],
      evidenceType: 'AnomalySingleEvent',
      score: 99,
      scoreContribution: 46,
      severity: 'Critical',
      top3eventsJsonStr: null,
      top3events: null,
      numOfEvents: 2,
      timeframe: 'Hourly',
      supportingInformation: null
    },
    {
      id: '277cdcca-4a55-4d0f-8a8e-750aee9c438f',
      entityType: 'User',
      entityTypeFieldName: null,
      entityName: 'critical',
      startDate: 1543183200000,
      endDate: 1543183200000,
      retentionDate: null,
      anomalyType: null,
      anomalyTypeFieldName: 'abnormal_active_directory_day_time_operation',
      name: 'abnormal_active_directory_day_time_operation',
      anomalyValue: '2018-11-25T22:00:00Z',
      dataEntitiesIds: ['active_directory'],
      evidenceType: 'AnomalySingleEvent',
      score: 71,
      scoreContribution: 33,
      severity: 'Critical',
      top3eventsJsonStr: null,
      top3events: null,
      numOfEvents: 2,
      timeframe: 'Hourly',
      supportingInformation: null
    },
    {
      id: 'fb8a98a7-be9a-4cbb-b95d-b4f0c3239225',
      entityType: 'User',
      entityTypeFieldName: null,
      entityName: 'critical',
      startDate: 1543183200000,
      endDate: 1543186800000,
      retentionDate: null,
      anomalyType: null,
      anomalyTypeFieldName:
        'high_number_of_successful_object_change_operations',
      name: 'high_number_of_successful_object_change_operations',
      anomalyValue: '10.0',
      dataEntitiesIds: ['active_directory'],
      evidenceType: 'AnomalyAggregatedEvent',
      score: 43,
      scoreContribution: 20,
      severity: 'Critical',
      top3eventsJsonStr: null,
      top3events: null,
      numOfEvents: 10,
      timeframe: 'Hourly',
      supportingInformation: null
    }
  ],
  evidenceSize: 3,
  score: 100,
  severityCode: 0,
  severity: 'Critical',
  status: 'Closed',
  feedback: 'Rejected',
  userScoreContribution: 20.0,
  userScoreContributionFlag: true,
  timeframe: 'Hourly',
  dataSourceAnomalyTypePair: null
};