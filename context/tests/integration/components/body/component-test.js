import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import DatasourceList from 'context/config/im-columns';

moduleForComponent('context-panel/body', 'Integration | Component | context-panel/body', {
  integration: true
});

test('it renders', function(assert) {
  const data = { 'Alerts': [{
    'resultId': '5b633727-ca0c-4c6e-b869-db65b1256fa8\/2',
    'queryId': 'd736d47c-8f2a-45b3-b4ad-1b88d6044af8',
    'datasourceId': '57c7e594b3b318b147f79fc6',
    'dataSourceName': 'IMConn_Alerts_datasource',
    'dataSourceType': 'Alerts',
    'resultList': [
      {
        '_id': {
          '$oid': '56b2f16de4b07ebea57c4a3c'
        },
        'alert': {
          'risk_score': 90.0,
          'source': 'Reporting Engine',
          'name': 'RERULE',
          'numEvents': 41,
          'timestamp': {
            '$date': '2016-02-04T06:36:28.000Z'
          }
        },
        'incidentId': 'INC-132'
      },
      {
        '_id': {
          '$oid': '56b429f5e4b0a1500b38521f'
        },
        'alert': {
          'source': 'Reporting Engine',
          'timestamp': {
            '$date': '2016-02-05T04:49:57.000Z'
          },
          'risk_score': 90.0,
          'name': 'RERULE',
          'numEvents': 100
        },
        'incidentId': 'INC-134'
      },
      {
        '_id': {
          '$oid': '56b43263e4b0a1500b385321'
        },
        'alert': {
          'source': 'Reporting Engine',
          'timestamp': {
            '$date': '2016-02-05T05:25:55.000Z'
          },
          'risk_score': 90.0,
          'name': 'RERULE',
          'numEvents': 93
        },
        'incidentId': 'INC-134'
      },
      {
        '_id': {
          '$oid': '56b96a8fe4b0a1500b385452'
        },
        'alert': {
          'source': 'Reporting Engine',
          'timestamp': {
            '$date': '2016-02-09T04:26:55.000Z'
          },
          'risk_score': 90.0,
          'name': 'RERULE',
          'numEvents': 65
        },
        'incidentId': 'INC-230'
      },
      {
        '_id': {
          '$oid': '56b96e8be4b0a1500b38545c'
        },
        'alert': {
          'source': 'Reporting Engine',
          'timestamp': {
            '$date': '2016-02-09T04:43:55.000Z'
          },
          'risk_score': 90.0,
          'name': 'RERULE',
          'numEvents': 65
        },
        'incidentId': 'INC-230'
      },
      {
        '_id': {
          '$oid': '56b2f16de4b07ebea57c4a3c'
        },
        'alert': {
          'risk_score': 90.0,
          'source': 'Reporting Engine',
          'name': 'RERULE',
          'numEvents': 41,
          'timestamp': {
            '$date': '2016-02-04T06:36:28.000Z'
          }
        },
        'incidentId': 'INC-132'
      },
      {
        '_id': {
          '$oid': '56b429f5e4b0a1500b38521f'
        },
        'alert': {
          'source': 'Reporting Engine',
          'timestamp': {
            '$date': '2016-02-05T04:49:57.000Z'
          },
          'risk_score': 90.0,
          'name': 'RERULE',
          'numEvents': 100
        },
        'incidentId': 'INC-134'
      },
      {
        '_id': {
          '$oid': '56b43263e4b0a1500b385321'
        },
        'alert': {
          'source': 'Reporting Engine',
          'timestamp': {
            '$date': '2016-02-05T05:25:55.000Z'
          },
          'risk_score': 90.0,
          'name': 'RERULE',
          'numEvents': 93
        },
        'incidentId': 'INC-134'
      }

    ],
    'pagingEnabled': false,
    'currentPageId': 0,
    'failed': false,
    'errorMessage': null,
    'maxPages': 0
  }] };

  this.set('alertsData', data);
  this.set('columns', DatasourceList);
  this.render(hbs`{{context-panel/body contextData=alertsData datasourceList=columns tabdata='overview'}}`);
  assert.equal(this.$('.rsa-data-table-header-cell').length, 6, 'Testing count of data header cells');


});
