export const lookupData = [
  {
    Alerts: {
      'resultId': 'NONE',
      'queryId': 'fc1f69c4-0227-45c0-8d69-0ee9575e291f',
      'datasourceId': '5ac318fa922aa33a1c9e704b',
      'dataSourceName': '5ac318fa922aa33a1c9e7048_Alerts_datasource',
      'dataSourceDescription': null,
      'dataSourceType': 'Alerts',
      'dataSourceGroup': 'Alerts',
      'connectionName': 'SAUII - Respond Server',
      'dataSourceCreatedOn': 1522735354980,
      'dataSourceLastModifiedOn': 1526374418997,
      'contentLastModifiedOn': 1526369400150,
      'resultList': [
        {
          '_id': {
            '$oid': '5af96e4626362f0bbbfae129'
          },
          'receivedTime': {
            '$date': '2018-05-14T11:08:54.851Z'
          },
          'alert': {
            'source': 'Event Stream Analysis',
            'timestamp': {
              '$date': '2018-05-14T11:08:54.000Z'
            },
            'risk_score': 70,
            'name': 'Autorun Unsigned Hidden',
            'numEvents': 1
          },
          'incidentId': 'INC-1227'
        }
      ],
      'resultMeta': {
        'timeQuerySubmitted': 1526381176784,
        'sorting.sortOrder[1].propertyName': 'alert.severity',
        'sorting.sortOrder[1].direction': 'DESC',
        'sorting.sortOrder[0].direction': 'DESC',
        'timeFilter.timeUnit': 'DAY',
        'timeFilter.propertyName': 'receivedTime',
        'timeFilter.timeUnitCount': 7,
        'limit': 200,
        'timeFilter.absoluteStartTime': true,
        'sorting.sortOrder[0].propertyName': 'alert.timestamp',
        'dataSourceCreatedBy': 'admin'
      },
      'pagingEnabled': false,
      'currentPageId': 0,
      'failed': false,
      'errorMessage': null,
      'maxPages': 0,
      'timeQuerySubmitted': 1526381176784,
      'totalContentCount': 0
    },
    Incidents: {
      'resultId': 'NONE',
      'queryId': 'fc1f69c4-0227-45c0-8d69-0ee9575e291f',
      'datasourceId': '5ac318fa922aa33a1c9e704a',
      'dataSourceName': '5ac318fa922aa33a1c9e7048_Incidents_datasource',
      'dataSourceDescription': null,
      'dataSourceType': 'Incidents',
      'dataSourceGroup': 'Incidents',
      'connectionName': 'SAUII - Respond Server',
      'dataSourceCreatedOn': 1522735354841,
      'dataSourceLastModifiedOn': 1526374418928,
      'contentLastModifiedOn': 1526369400156,
      'resultList': [
        {
          '_id': 'INC-1229',
          'name': 'EDR Incident for PRSER',
          'priority': 'LOW',
          'status': 'NEW',
          'alertCount': 1,
          'averageAlertRiskScore': 70,
          'created': {
            '$date': '2018-05-14T11:39:57.401Z'
          }
        },
        {
          '_id': 'INC-1227',
          'name': 'EDR Incident for PRSER',
          'priority': 'LOW',
          'status': 'NEW',
          'alertCount': 1,
          'averageAlertRiskScore': 70,
          'created': {
            '$date': '2018-05-14T11:08:55.140Z'
          }
        }
      ],
      'resultMeta': {
        'timeQuerySubmitted': 1526381176830,
        'sorting.sortOrder[1].propertyName': 'prioritySort',
        'sorting.sortOrder[1].direction': 'DESC',
        'sorting.sortOrder[0].direction': 'DESC',
        'timeFilter.timeUnit': 'DAY',
        'root': null,
        'timeFilter.propertyName': 'receivedTime',
        'timeFilter.timeUnitCount': 7,
        'limit': 50,
        'timeFilter.absoluteStartTime': true,
        'sorting.sortOrder[0].propertyName': 'created',
        'dataSourceCreatedBy': 'admin'
      },
      'pagingEnabled': false,
      'currentPageId': 0,
      'failed': false,
      'errorMessage': null,
      'maxPages': 0,
      'timeQuerySubmitted': 1526381176830,
      'totalContentCount': 0
    }
  }
];