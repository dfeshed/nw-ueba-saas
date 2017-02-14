/* eslint-env node */

module.exports = [
  {
    'resultId': 'd9a05604-e8aa-4566-bb4a-d01f95aeb8f3/3',
    'queryId': 'cbceadd8-44a2-4833-9543-73eb2f7e2c31',
    'datasourceId': '58216e7cd4c6f6a31ae9ccb1',
    'dataSourceName': 'LiveConnectTest01_LiveConnect-Ip_datasource',
    'dataSourceDescription': null,
    'dataSourceType': 'LiveConnect-Ip',
    'resultList': [
      {
        'record': [
          {
            'RelatedIps': {
              'id': 'rsa.com',
              'risk': null,
              'ips': [
                {
                  'id': '210.210.210.23',
                  'risk': 'SAFE',
                  'domain': 'g00gle.com',
                  'asnNumber': 'AS1479141516188',
                  'asnPrefix': 'hceelmxyw',
                  'countryCode': 'LR',
                  'country': 'Liberia',
                  'asnOrganization': 'hceelmxyw'
                },
                {
                  'id': '210.20.210.23',
                  'risk': 'UNSAFE',
                  'domain': 'yah00.com',
                  'asnNumber': 'AS1479141516195',
                  'asnPrefix': 'hceelmxzd',
                  'countryCode': 'PT',
                  'country': 'Portugal',
                  'asnOrganization': 'hceelmxzd'
                }
              ]
            }
          }
        ]
      }
    ],
    'pagingEnabled': false,
    'currentPageId': 0,
    'failed': false,
    'errorMessage': null,
    'tags': [],
    'maxPages': 0
  },
  {
    'resultId': 'd9a05604-e8aa-4566-bb4a-d01f95aeb8f3/3',
    'queryId': 'cbceadd8-44a2-4833-9543-73eb2f7e2c31',
    'datasourceId': '58216e7cd4c6f6a31ae9ccb1',
    'dataSourceName': 'LiveConnectTest01_LiveConnect-Ip_datasource',
    'dataSourceDescription': null,
    'dataSourceType': 'LiveConnect-Domain',
    'resultList': [
      {
        'record': [
          {
            'RelatedDomains': {
              'id': '1.1.1.1',
              'risk': null,
              'domains': [
                {
                  'id': 'Thisdomainsmailidistooolongfortestingweareusingthisfoo.com',
                  'risk': 'SAFE',
                  'countryCode': 'UK',
                  'country': 'United Kingdom',
                  'creationDate': 1475002318162,
                  'expiredDate': 1425002318162,
                  'registrantEmail': 'far@foo.com'
                },
                {
                  'id': 'Thisdomainsmailidistooolongfortestingweareusingthisfoo.com',
                  'risk': 'UNSAFE',
                  'countryCode': 'US',
                  'country': 'United States',
                  'creationDate': 1475002318162,
                  'expiredDate': 1415002318162,
                  'registrantEmail': 'bar@foo.com'
                }
              ]
            }
          }
        ]
      }
    ],
    'pagingEnabled': false,
    'currentPageId': 0,
    'failed': false,
    'errorMessage': null,
    'tags': [],
    'maxPages': 0
  },
  {
    'resultId': 'd9a05604-e8aa-4566-bb4a-d01f95aeb8f3/3',
    'queryId': 'cbceadd8-44a2-4833-9543-73eb2f7e2c31',
    'datasourceId': '58216e7cd4c6f6a31ae9ccb1',
    'dataSourceName': 'LiveConnectTest01_LiveConnect-Ip_datasource',
    'dataSourceDescription': null,
    'dataSourceType': 'LiveConnect-File',
    'resultList': [
      {
        'record': [
          {
            'RelatedFiles': {
              'id': '1.1.1.1',
              'risk': null,
              'files': [
                {
                  'id': '1a708f247cc6a7364b873c029bbdf459',
                  'risk': 'UNKNOWN',
                  'unique': false,
                  'fileName': 'filename1',
                  'impHash': 'c6a7364b873c029',
                  'compileTime': 1401972584000
                },
                {
                  'id': '2a708f247cc6a7364b873c029bbdf459',
                  'risk': 'UNSAFE',
                  'unique': true,
                  'fileName': 'filename2',
                  'impHash': 'c6a7364b873c030',
                  'compileTime': 1401972584000
                }
              ]
            }
          }
        ]
      }
    ],
    'pagingEnabled': false,
    'currentPageId': 0,
    'failed': false,
    'errorMessage': null,
    'tags': [],
    'maxPages': 0
  }
];