import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  fileCount,
  hasFiles,
  fileExportLink,
  serviceList,
  isAllSelected,
  selectedFileStatusHistory,
  hostList,
  files,
  areFilesLoading,
  isExportButtonDisabled,
  fileTotalLabel,
  nextLoadCount,
  isAnyFileFloatingOrMemoryDll,
  fileDownloadButtonStatus,
  downloadLink,
  isCertificateViewDisabled,
  hostListCount
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
        hostNameList: { data: [{ 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC', 'hostname': 'Machine1', 'score': 0 }] }
      }
    }
  });
  const result = hostList(state);
  const name = result[0].hostname;
  assert.equal(name, 'Machine1');
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
  assert.equal(result1.disabled, true, 'export button is disabled');

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
  assert.equal(result2.disabled, true, 'export button is disabled');

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
  assert.equal(result3.disabled, false, 'export button is enabled');
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

test('isAnyFileFloatingOrMemoryDll', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        fileData: { 1: { id: 1 }, 2: { id: 2 } },
        selectedFileList: [
          { id: 1,
            format: 'floating'
          },
          {
            id: 2
          }
        ]
      }
    }
  });
  const result = isAnyFileFloatingOrMemoryDll(state);
  assert.equal(result, true);
});

test('fileDownloadButtonStatus', function(assert) {
  const state1 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        selectedFileList: []
      }
    }
  });
  const result1 = fileDownloadButtonStatus(Immutable.from(state1));
  assert.deepEqual(result1.isDownloadToServerDisabled, true, 'selectedFile is empty');

  const state2 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        selectedFileList: [{}, {}]
      }
    }
  });
  const result2 = fileDownloadButtonStatus(Immutable.from(state2));
  assert.deepEqual(result2.isDownloadToServerDisabled, true, 'selectedFileList is of length 2');
  assert.deepEqual(result2.isSaveLocalAndFileAnalysisDisabled, true);

  const state3 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        selectedFileList: [{ format: 'pe', signature: { features: ['signed'] }, downloadInfo: { status: 'Downloaded' } }]
      }
    }
  });

  const result3 = fileDownloadButtonStatus(Immutable.from(state3));
  assert.deepEqual(result3.isDownloadToServerDisabled, true, 'file is downloaded');
  assert.deepEqual(result3.isSaveLocalAndFileAnalysisDisabled, false);

  const state4 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        selectedFileList: [{ format: 'floating' }]
      }
    }
  });
  const result4 = fileDownloadButtonStatus(Immutable.from(state4));
  assert.deepEqual(result4.isDownloadToServerDisabled, true, 'format is floating');
  assert.deepEqual(result4.isSaveLocalAndFileAnalysisDisabled, true);

  const state5 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        selectedFileList: [{ format: 'pe' }]
      }
    }
  });
  const result5 = fileDownloadButtonStatus(Immutable.from(state5));
  assert.deepEqual(result5.isDownloadToServerDisabled, false);

  const state6 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        selectedFileList: [{ signature: { features: ['file.memoryHash'] } }]
      }
    }
  });
  const result6 = fileDownloadButtonStatus(Immutable.from(state6));
  assert.deepEqual(result6.isDownloadToServerDisabled, true);

  const state7 = Immutable.from({
    files: {
      filter: {
      },
      fileList: {
        selectedFileList: [{ signature: { features: ['signed'] } }]
      }
    }
  });
  const result7 = fileDownloadButtonStatus(Immutable.from(state7));
  assert.deepEqual(result7.isDownloadToServerDisabled, false);
});

test('downloadLink for save local copy', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        downloadLink: '/test/test.zip'
      }
    }
  });
  const result = downloadLink(state);
  assert.equal(result.includes('/test/test.zip'), true);
});

test('downloadLink for save local copy when null', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        downloadLink: null
      }
    }
  });
  const result = downloadLink(state);
  assert.equal(result, null);
});
test('isCertificateViewDisabled ', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        selectedFileList: [{ signature: { features: ['signed'] } }]
      }
    },
    endpointServer: {},
    endpointQuery: {}
  });
  const result1 = isCertificateViewDisabled(state);
  const result2 = isCertificateViewDisabled(Immutable.from({
    files: {
      fileList: {
        selectedFileList: [{ signature: { features: ['unsigned'], thumbprint: 'test' } }, { signature: { features: ['unsigned'] } }]
      }
    },
    endpointServer: {},
    endpointQuery: {}
  })
  );
  const result3 = isCertificateViewDisabled(Immutable.from({
    files: {
      fileList: {
        selectedFileList: [{ signature: { features: ['unsigned'], thumbprint: 'test' } }, { signature: { features: ['unsigned'] } }]
      }
    },
    endpointServer: {},
    endpointQuery: {}
  })
  );
  const selectedFileListData = [
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } },
    { signature: { features: ['signed'], thumbprint: 'test' } }
  ];
  const result4 = isCertificateViewDisabled(Immutable.from({
    files: {
      fileList: {
        selectedFileList: selectedFileListData
      }
    },
    endpointServer: {},
    endpointQuery: {}
  }));
  assert.equal(result1, true);
  assert.equal(result2, true);
  assert.equal(result3, true);
  assert.equal(result4, true);
});

test('hostListCount test', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        selectedDetailFile: {
          hostCount: 1
        }
      }
    }
  });
  const result = hostListCount(state);
  assert.equal(result, 1);
});

test('isCertificateViewDisabled returns false when service is down', function(assert) {
  const state = Immutable.from({
    files: {
      fileList: {
        hostNameList: []
      }
    },
    endpointQuery: {
      serverId: 1
    },
    endpointServer: {
      isSummaryRetrieveError: true,
      serviceData: [
        {
          id: 2,
          isServerOnline: true
        },
        {
          id: 1,
          isServerOnline: false
        }
      ]
    }
  });
  const result = isCertificateViewDisabled(state);
  assert.equal(result, true);
});

