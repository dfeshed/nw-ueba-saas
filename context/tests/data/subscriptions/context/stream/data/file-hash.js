export default [
  {
    'resultId': '96f76ccd-f985-4517-926a-1324e423b798/2',
    'queryId': '7ca0265c-fc5e-48a4-9c62-df60d86d655a',
    'datasourceId': '5bab201c8f7904a3244a50b6',
    'dataSourceName': '5bab201c8f7904a3244a50b5_FileReputationServer_datasource',
    'dataSourceDescription': null,
    'dataSourceType': 'FileReputationServer',
    'dataSourceGroup': 'FileReputationServer',
    'connectionName': 'FileReputationServer',
    'dataSourceCreatedOn': 1537941532213,
    'dataSourceLastModifiedOn': 1537941532213,
    'contentLastModifiedOn': 0,
    'resultList': [ {
      'scannerMatch': 25,
      'type': 'Trojan',
      'platform': 'Win32',
      'familyName': 'Swizzor',
      'status': 'Malicious'
    } ],
    'resultMeta': {
      'timeQuerySubmitted': 1537941763385,
      'root': null,
      'limit': 0,
      'dataSourceCreatedBy': 'local'
    },
    'pagingEnabled': false,
    'currentPageId': 0,
    'failed': false,
    'errorMessage': null,
    'warning': { },
    'maxPages': 0,
    'timeQuerySubmitted': 1537941763385,
    'totalContentCount': 0,
    'order': [ 'status', 'scannerMatch', 'familyName', 'platform', 'type' ]
  },
  {
    'resultId': '32e897ec-3dfe-47ae-b32a-f88893d0b42a/1',
    'queryId': 'daaeae43-9301-4fa3-84e5-7c1675c653ae',
    'datasourceId': '58e34255c5c9703a4b124b9d',
    'dataSourceName': 'BlackList',
    'dataSourceDescription': 'BlackList-IP',
    'dataSourceType': 'LIST',
    'dataSourceGroup': 'LIST',
    'connectionName': null,
    'dataSourceCreatedOn': 1491288661700,
    'dataSourceLastModifiedOn': 1491288661700,
    'contentLastModifiedOn': 1491289221500,
    'resultList': [
      {
        'createdBy': 'admin',
        'createdOn': 1491288661876,
        'lastModifiedOn': 1491288661876,
        'id': 'ad48ecc7-a034-4be2-a01f-63b64150aad2',
        'data': {
          'LIST': '10.101.47.107'
        }
      }
    ],
    'resultMeta': {
      'timeQuerySubmitted': 1491289231023,
      'dataSourceCreatedBy': 'User'
    },
    'pagingEnabled': false,
    'currentPageId': 0,
    'failed': false,
    'errorMessage': null,
    'maxPages': 0,
    'timeQuerySubmitted': 1491289231023
  }
];
