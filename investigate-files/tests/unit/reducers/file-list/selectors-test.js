import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  fileCount,
  hasFiles,
  fileExportLink,
  serviceList,
  getContext,
  isAllSelected,
  selectedFileStatusHistory,
  hostList,
  files,
  areFilesLoading,
  isExportButtonDisabled,
  fileTotalLabel,
  nextLoadCount
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

test('files', function(assert) {
  const result = files(STATE);
  assert.equal(result.length, 3);
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

test('getContext returns incidents', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        lookupData: [{
          Incidents: {
            resultList: [{ _id: 'INC-18409', name: 'RespondAlertsESA for user199' }]
          }
        }]
      },
      visuals: {
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
        ]
      },
      visuals: {
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

test('areFilesLoading returns true', function(assert) {
  const result = areFilesLoading({
    files: {
      fileList: {
        areFilesLoading: 'wait'
      }
    }
  });
  assert.equal(result, true);
});

test('areFilesLoading returns false', function(assert) {
  const result = areFilesLoading({
    files: {
      fileList: {
        areFilesLoading: 'completed'
      }
    }
  });
  assert.equal(result, false);
});

test('isExportButtonDisabled', function(assert) {
  const state1 = Immutable.from({
    files: {
      fileList: {}
    },
    endpointServer: {},
    endpointQuery: {}
  });
  const result1 = isExportButtonDisabled(state1);
  assert.equal(result1, true, 'export button is disabled');

  const state2 = Immutable.from({
    files: STATE.files,
    endpointServer: {
      serviceData: [{ id: '123', name: 'endpoint-broker-server' }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result2 = isExportButtonDisabled(state2);
  assert.equal(result2, true, 'export button is disabled');

  const state3 = Immutable.from({
    files: STATE.files,
    endpointServer: {
      serviceData: [{ id: '123', name: 'endpoint-server' }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result3 = isExportButtonDisabled(state3);
  assert.equal(result3, false, 'export button is enabled');
});

test('fileTotalLabel', function(assert) {
  const state1 = Immutable.from({
    files: {
      fileList: { totalItems: 1000 },
      filter: {}
    },
    endpointServer: { serviceData: [{ id: '123', name: 'endpoint-broker-server' }] },
    endpointQuery: { serverId: '123' }
  });
  const result1 = fileTotalLabel(state1);
  assert.equal(result1, '1000+');

  const state2 = Immutable.from({
    files: {
      fileList: { totalItems: 1278 },
      filter: { expressionList: Array(1) }
    },
    endpointServer: {
      serviceData: [{ id: '123', name: 'endpoint-server' }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result2 = fileTotalLabel(state2);
  assert.equal(result2, '1278');

  const state3 = Immutable.from({
    files: {
      fileList: { totalItems: 1278, hasNext: true },
      filter: { expressionList: Array(1) }
    },
    endpointServer: {
      serviceData: [{ id: '123', name: 'endpoint-server' }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result3 = fileTotalLabel(state3);
  assert.equal(result3, '1000+');

  const state4 = Immutable.from({
    files: {
      fileList: { totalItems: 1299 },
      filter: {}
    },
    endpointServer: {},
    endpointQuery: {}
  });
  const result4 = fileTotalLabel(state4);
  assert.equal(result4, '1299');
});
test('nextLoadCount', function(assert) {
  const result1 = nextLoadCount(STATE);
  assert.equal(result1, 3);
  const fileData = new Array(101)
    .join().split(',')
    .map(function(item, index) {
      return { index: { id: ++index, checksumSha256: index } };
    });
  const STATE1 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        totalItems: 101,
        fileData
      }
    }
  });
  const result2 = nextLoadCount(STATE1);
  assert.equal(result2, 100);
});