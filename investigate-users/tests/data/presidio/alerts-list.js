export default {
  'data': [{
    'id': '3af8801b-0979-4066-b906-6330eaca2337',
    'name': 'non_standard_hours',
    'startDate': 1534114800000,
    'endDate': 1534118400000,
    'entityType': 'User',
    'entityName': 'ad_qa_1_9',
    'entityId': '0b5fce2a-c73c-467b-868a-c4c6e0c41027',
    'evidences': [{
      'id': '1486f9ac-974d-4be6-8641-1b0826097854',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534114800000,
      'endDate': 1534114800000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Change Time',
      'anomalyTypeFieldName': 'abnormal_active_directory_day_time_operation',
      'name': 'Abnormal Active Directory Change Time',
      'anomalyValue': '2018-08-12T23:00:00Z',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 98,
      'scoreContribution': 32,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': '574c802c-e4fa-4686-a31e-a35380916ec8',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534114800000,
      'endDate': 1534118400000,
      'retentionDate': null,
      'anomalyType': 'Multiple Active Directory Object Changes',
      'anomalyTypeFieldName': 'high_number_of_successful_object_change_operations',
      'name': 'Multiple Active Directory Object Changes',
      'anomalyValue': '16.0',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalyAggregatedEvent',
      'score': 54,
      'scoreContribution': 16,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 16,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': '4ec0579d-537d-4ab5-97a1-fe25a52bd440',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115520000,
      'endDate': 1534115520000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Object Change',
      'anomalyTypeFieldName': 'abnormal_object_change_operation',
      'name': 'Abnormal Active Directory Object Change',
      'anomalyValue': 'SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 26,
      'scoreContribution': 6,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'd2cf1ee9-bb79-485b-967e-40c325d1f4dc',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115640000,
      'endDate': 1534115640000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Object Change',
      'anomalyTypeFieldName': 'abnormal_object_change_operation',
      'name': 'Abnormal Active Directory Object Change',
      'anomalyValue': 'SECURITY_ENABLED_GLOBAL_GROUP_DELETED',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 26,
      'scoreContribution': 6,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': '17be0517-4ef3-4d2f-94a3-9683383fd0ce',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115820000,
      'endDate': 1534115820000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Object Change',
      'anomalyTypeFieldName': 'abnormal_object_change_operation',
      'name': 'Abnormal Active Directory Object Change',
      'anomalyValue': 'COMPUTER_ACCOUNT_CREATED',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 26,
      'scoreContribution': 6,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'f370e256-bcd5-4f0b-b44f-ae9eba795608',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115700000,
      'endDate': 1534115700000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Object Change',
      'anomalyTypeFieldName': 'abnormal_object_change_operation',
      'name': 'Abnormal Active Directory Object Change',
      'anomalyValue': 'SECURITY_ENABLED_LOCAL_GROUP_CREATED',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 26,
      'scoreContribution': 6,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'efc76a2e-633f-4f15-afd5-63af3a8f577f',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534114980000,
      'endDate': 1534114980000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Object Change',
      'anomalyTypeFieldName': 'abnormal_object_change_operation',
      'name': 'Abnormal Active Directory Object Change',
      'anomalyValue': 'SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 26,
      'scoreContribution': 6,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'f089be2c-2bcd-41ab-ac4a-70dfb6009880',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115880000,
      'endDate': 1534115880000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Object Change',
      'anomalyTypeFieldName': 'abnormal_object_change_operation',
      'name': 'Abnormal Active Directory Object Change',
      'anomalyValue': 'COMPUTER_ACCOUNT_CHANGED',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 26,
      'scoreContribution': 6,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': '8e86d51f-8a15-4961-9c34-c57b1a43d40d',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115280000,
      'endDate': 1534115280000,
      'retentionDate': null,
      'anomalyType': 'User account disabled',
      'anomalyTypeFieldName': 'user_account_disabled',
      'name': 'User account disabled',
      'anomalyValue': '',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'StaticIndicator',
      'score': 10,
      'scoreContribution': 3,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'f9530568-6477-41e4-b4ae-27200109f5ef',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115160000,
      'endDate': 1534115160000,
      'retentionDate': null,
      'anomalyType': 'User password changed',
      'anomalyTypeFieldName': 'user_password_changed',
      'name': 'User password changed',
      'anomalyValue': '',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'StaticIndicator',
      'score': 10,
      'scoreContribution': 3,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': '66ac38ad-e2cf-43e9-8d46-559f5562306a',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115100000,
      'endDate': 1534115100000,
      'retentionDate': null,
      'anomalyType': 'User account enabled',
      'anomalyTypeFieldName': 'user_account_enabled',
      'name': 'User account enabled',
      'anomalyValue': '',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'StaticIndicator',
      'score': 10,
      'scoreContribution': 3,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'ccae21ff-a6e0-4837-b9b2-848937ed417f',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115220000,
      'endDate': 1534115220000,
      'retentionDate': null,
      'anomalyType': 'user_password_reset',
      'anomalyTypeFieldName': 'user_password_reset',
      'name': 'user_password_reset',
      'anomalyValue': '',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'StaticIndicator',
      'score': 10,
      'scoreContribution': 3,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': '3cafd3dd-d675-4714-b3c8-ec3395c77a54',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534114800000,
      'endDate': 1534114800000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Object Change',
      'anomalyTypeFieldName': 'abnormal_object_change_operation',
      'name': 'Abnormal Active Directory Object Change',
      'anomalyValue': 'SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 1,
      'scoreContribution': 0,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'a960d0d8-cad4-4eca-bb08-8834862b5d99',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'ad_qa_1_9',
      'startDate': 1534115460000,
      'endDate': 1534115460000,
      'retentionDate': null,
      'anomalyType': 'User account unlocked',
      'anomalyTypeFieldName': 'user_account_unlocked',
      'name': 'User account unlocked',
      'anomalyValue': '',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'StaticIndicator',
      'score': 10,
      'scoreContribution': 0,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }],
    'evidenceSize': 14,
    'score': 97,
    'severityCode': 1,
    'severity': 'Low',
    'status': 'Open',
    'feedback': 'None',
    'userScoreContribution': 15.0,
    'userScoreContributionFlag': true,
    'timeframe': 'Hourly',
    'dataSourceAnomalyTypePair': null
  }, {
    'id': '71e3da1c-28fa-4510-859e-42a04306552d',
    'name': 'multiple_logons_by_user',
    'startDate': 1534111200000,
    'endDate': 1534114800000,
    'entityType': 'User',
    'entityName': 'auth_qa_1_14',
    'entityId': '5f4c937f-705d-4722-86f2-d67b35c91808',
    'evidences': [{
      'id': '63287ad0-35b7-4edf-ba2b-71d921ba6e68',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'auth_qa_1_14',
      'startDate': 1534112880000,
      'endDate': 1534112880000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Logon Time',
      'anomalyTypeFieldName': 'abnormal_logon_day_time',
      'name': 'Abnormal Logon Time',
      'anomalyValue': '2018-08-12T22:28:00Z',
      'dataEntitiesIds': [
        'authentication'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 98,
      'scoreContribution': 49,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'ed1e2c22-4809-4e22-8be5-5b577098a7a2',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'auth_qa_1_14',
      'startDate': 1534111200000,
      'endDate': 1534114800000,
      'retentionDate': null,
      'anomalyType': 'Multiple Successful Authentications',
      'anomalyTypeFieldName': 'high_number_of_successful_authentications',
      'name': 'Multiple Successful Authentications',
      'anomalyValue': '60.0',
      'dataEntitiesIds': [
        'authentication'
      ],
      'evidenceType': 'AnomalyAggregatedEvent',
      'score': 100,
      'scoreContribution': 48,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 60,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'a9f624db-3338-4d76-a53f-95ff200046b1',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'auth_qa_1_14',
      'startDate': 1534111200000,
      'endDate': 1534111200000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Computer',
      'anomalyTypeFieldName': 'abnormal_source_machine',
      'name': 'Abnormal Computer',
      'anomalyValue': 'qIKvBySj_SRC',
      'dataEntitiesIds': [
        'authentication'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 4,
      'scoreContribution': 2,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 40,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }],
    'evidenceSize': 3,
    'score': 93,
    'severityCode': 1,
    'severity': 'High',
    'status': 'Open',
    'feedback': 'None',
    'userScoreContribution': 15.0,
    'userScoreContributionFlag': true,
    'timeframe': 'Hourly',
    'dataSourceAnomalyTypePair': null
  }, {
    'id': '8db24110-f914-4dec-a66d-056fd1d449a5',
    'name': 'non_standard_hours',
    'startDate': 1534111200000,
    'endDate': 1534114800000,
    'entityType': 'User',
    'entityName': 'mixed_high_score_qa_1_1',
    'entityId': '762c11bc-59b3-41f6-9994-94ddc47bb522',
    'evidences': [{
      'id': 'bd564ba9-a66d-4ba7-a517-978a7450e516',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'mixed_high_score_qa_1_1',
      'startDate': 1534111200000,
      'endDate': 1534111200000,
      'retentionDate': null,
      'anomalyType': 'Abnormal File Access Time',
      'anomalyTypeFieldName': 'abnormal_event_day_time',
      'name': 'Abnormal File Access Time',
      'anomalyValue': '2018-08-12T22:00:00Z',
      'dataEntitiesIds': [
        'file'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 8,
      'scoreContribution': 52,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 2,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': 'c10d24fb-a9a4-4ff3-9850-545a58bfc29f',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'mixed_high_score_qa_1_1',
      'startDate': 1534111200000,
      'endDate': 1534111200000,
      'retentionDate': null,
      'anomalyType': 'Abnormal Active Directory Change Time',
      'anomalyTypeFieldName': 'abnormal_active_directory_day_time_operation',
      'name': 'Abnormal Active Directory Change Time',
      'anomalyValue': '2018-08-12T22:00:00Z',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'AnomalySingleEvent',
      'score': 67,
      'scoreContribution': 41,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }, {
      'id': '166e100c-bc4a-4833-9c7d-dd2d195d5381',
      'entityType': 'User',
      'entityTypeFieldName': null,
      'entityName': 'mixed_high_score_qa_1_1',
      'startDate': 1534111200000,
      'endDate': 1534111200000,
      'retentionDate': null,
      'anomalyType': 'user_password_reset',
      'anomalyTypeFieldName': 'user_password_reset',
      'name': 'user_password_reset',
      'anomalyValue': '',
      'dataEntitiesIds': [
        'active_directory'
      ],
      'evidenceType': 'StaticIndicator',
      'score': 10,
      'scoreContribution': 6,
      'severity': 'Critical',
      'top3eventsJsonStr': null,
      'top3events': null,
      'numOfEvents': 1,
      'timeframe': 'Hourly',
      'supportingInformation': null
    }],
    'evidenceSize': 3,
    'score': 90,
    'severityCode': 2,
    'severity': 'Critical',
    'status': 'Open',
    'feedback': 'None',
    'userScoreContribution': 10.0,
    'userScoreContributionFlag': true,
    'timeframe': 'Hourly',
    'dataSourceAnomalyTypePair': null
  }],
  'total': 424,
  'offset': 0,
  'warning': null,
  'info': {
    'total_severity_count': {
      'Critical': 41,
      'High': 58,
      'Low': 236,
      'Medium': 89
    }
  }
};