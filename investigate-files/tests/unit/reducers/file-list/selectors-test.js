import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  fileCount,
  hasFiles,
  fileExportLink,
  fileCountForDisplay,
  serviceList,
  getDataSourceTab,
  getAlertsCount,
  getIncidentsCount,
  getContext
} from 'investigate-files/reducers/file-list/selectors';

module('Unit | selectors | file-list');

const STATE = Immutable.from({
  files: {
    filter: {
    },
    fileList: {
      totalItems: 3,
      files: [
        {
          'firstFileName': 'xt_conntrack.ko',
          'format': 'ELF'
        },
        {
          'firstFileName': 'svchost.dll',
          'format': 'PE'
        },
        {
          'firstFileName': 'explorer.dll',
          'format': 'PE'
        }
      ],
      downloadId: 123,
      listOfServices: []
    }
  }
});

test('fileExportLink', function(assert) {
  const result = fileExportLink(STATE);
  assert.equal(result, `${location.origin}/rsa/endpoint/file/property/download?id=123`, 'should return the export link');
});

test('fileCount', function(assert) {
  const result = fileCount(STATE);
  assert.equal(result, 3, 'fileCount selector returns the file list count');
});

test('hasFiles', function(assert) {
  const result = hasFiles(STATE);
  assert.equal(result, true, 'hasFiles is true');
});

test('fileCountForDisplay', function(assert) {
  const result = fileCountForDisplay(STATE);
  assert.equal(result, 3, 'expected 3 files');
  const newDisplay = fileCountForDisplay(Immutable.from({
    files: {
      filter: {
        expressionList: [
          {
            propertyName: 'firstFileName',
            propertyValues: [
              {
                value: 'windows'
              }
            ],
            restrictionType: 'IN'
          }
        ]
      },
      fileList: {
        totalItems: '1000',
        files: [...Array(2000)]
      }
    }
  }));
  assert.equal(newDisplay, '1000+', 'expected 1000+ files');
});

test('serviceList', function(assert) {
  const newState = serviceList(Immutable.from({
    files: {
      fileList: {
        listOfServices: [{ name: 'broker' }, { name: 'concentrator' }, { name: 'decoder' }, { name: 'testService' }]
      }
    }
  }));
  assert.deepEqual(newState, [{ name: 'broker' }, { name: 'concentrator' }, { name: 'decoder' }], 'List of supported services');

  const listOfServicesNull = serviceList(Immutable.from({
    files: {
      fileList: {
        listOfServices: null
      }
    }
  }));
  assert.deepEqual(listOfServicesNull, null, 'Supported services available is null');
});

test('getDataSourceTab', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        activeDataSourceTab: 'INCIDENT'
      }
    }
  });
  const result = getDataSourceTab(state).findBy('name', 'INCIDENT');
  assert.equal(result.selected, true, 'Incidents Tab should be selected');
});

test('getContext returns incidents', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        lookupData: [{
          Incidents: {
            resultList: [{ _id: 'INC-18409', name: 'RespondAlertsESA for user199' }]
          }
        }],
        activeDataSourceTab: 'INCIDENT'
      }
    }
  });
  const result = getContext(state);
  assert.equal(result.resultList.length, 1, '1 incidents are fetched');
  assert.equal(getIncidentsCount(state), 1, 'Incident count is correct');
});

test('getContext returns alerts', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        lookupData: [
          {
            Alerts: {
              resultList: [{
                alert: { source: 'Event Stream Analysis 1' },
                incidentId: 'INC-18409'
              },
              {
                alert: { source: 'Event Stream Analysis 2' },
                incidentId: 'INC-18410'
              }]
            }
          }
        ],
        activeDataSourceTab: 'ALERT'
      }
    }
  });
  const result = getContext(state);
  assert.equal(result.resultList.length, 2, '2 Alerts fetched');
  assert.equal(getAlertsCount(state), 2, 'Alerts count is correct');
});

