import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  fileCount,
  hasFiles,
  fileExportLink,
  fileCountForDisplay,
  serviceList,
  getDataSourceTab,
  getContext,
  isAllSelected,
  selectedFileStatusHistory,
  hostList
} from 'investigate-files/reducers/file-list/selectors';

module('Unit | selectors | file-list');

const STATE = Immutable.from({
  files: {
    filter: {
    },
    fileList: {
      totalItems: 3,
      fileData: {
        a: {
          'firstFileName': 'xt_conntrack.ko',
          'format': 'ELF'
        },
        ab: {
          'firstFileName': 'svchost.dll',
          'format': 'PE'
        },
        ca: {
          'firstFileName': 'explorer.dll',
          'format': 'PE'
        }
      },
      downloadId: 123,
      listOfServices: []
    }
  },
  endpointQuery: {}
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
  const fileData = {};
  for (let i = 0; i < 5000; i++) {
    fileData[i] = {};
  }
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
        fileData
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
        activeDataSourceTab: 'RISK_PROPERTIES'
      }
    }
  });
  const result = getDataSourceTab(state).findBy('name', 'RISK_PROPERTIES');
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
});

test('getContext returns alerts', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        lookupData: [
          {
            Alerts: {
              resultList: [{
                '_id': {
                  '$oid': '5afcffbedb7a8b75269a0040'
                },
                alert: { source: 'Event Stream Analysis 1' },
                incidentId: 'INC-18409'
              },
              {
                '_id': {
                  '$oid': '5afcffbedb7a8b75269a0041'
                },
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
});

test('isAllSelected test', function(assert) {
  const state1 = Immutable.from({
    files: {
      fileList: {
        files: [ { id: 1 }, { id: 2 } ]
      }
    }
  });

  const state2 = Immutable.from({
    files: {
      fileList: {
        fileData: { 1: { id: 1 }, 2: { id: 2 } },
        selectedFileList: [ { id: 1 }, { id: 2 } ]
      }
    }
  });
  const result1 = isAllSelected(state1);
  const result2 = isAllSelected(state2);
  assert.equal(result1, false, 'isAllSelected should be false');
  assert.equal(result2, true, 'isAllSelected should be false');
});
test('selectedFileStatusHistory test', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        selectedFileStatusHistory: [ { id: 1 }, { id: 2 } ]
      }
    }
  });
  const result = selectedFileStatusHistory(state);
  assert.equal(result.length, 2, '2 items expected');
});

test('fileExportLink when serverId is defined', function(assert) {
  const result = fileExportLink({ ...STATE, endpointQuery: { serverId: '123' } });
  assert.equal(result, `${location.origin}/rsa/endpoint/123/file/property/download?id=123`, 'should return the export link inlcuding serverId');
});

test('hostList test', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        hostNameList: [{ value: 'Machine1', count: 5 }]
      }
    }
  });
  const result = hostList(state);
  assert.equal(result, 'Machine1');
});
