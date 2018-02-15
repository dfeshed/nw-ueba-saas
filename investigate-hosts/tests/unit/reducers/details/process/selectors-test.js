import { module, test } from 'qunit';
import { processDetailsData, processTreeData, processListData } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | process');

import {
  isNavigatedFromExplore,
  getProcessData,
  processTree,
  noProcessData,
  enrichedDllData,
  processList,
  isProcessLoading
} from 'investigate-hosts/reducers/details/process/selectors';

test('getProcessData', function(assert) {
  const result = getProcessData(Immutable.from({
    endpoint: {
      process: {
        processDetails: processDetailsData
      }
    }
  }));
  assert.equal(result.process.signature, 'valid,signed,Apple');
});

test('processTree', function(assert) {
  const result = processTree(Immutable.from({
    endpoint: {
      process: {
        processList: processListData,
        processTree: processTreeData
      },
      explore: {
        selectedTab: 'process'
      }
    }
  }));
  assert.equal(result.length, 3);
});

test('isNavigatedFromExplore', function(assert) {
  const result = isNavigatedFromExplore(Immutable.from({
    endpoint: {
      process: {
        processList: processListData,
        processTree: processTreeData
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.equal(result, true);
});

test('noProcessData returns true when processTree is empty', function(assert) {
  const result = noProcessData(Immutable.from({
    endpoint: {
      process: {
        processTree: [],
        processList: []
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('noProcessData returns false when processTree is not empty', function(assert) {
  const result = noProcessData(Immutable.from({
    endpoint: {
      process: {
        processTree: processTreeData,
        processList: processListData
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('enrichedDllData without signature', function(assert) {
  const result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            fileProperties: {
              fileName: 'test.exe'
            }
          }
        ]
      }
    }
  }));
  assert.equal(result[0].signature, undefined);
});
test('enrichedDllData with signature', function(assert) {

  let result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            fileProperties: {
              fileName: 'test.exe',
              signature: {
                features: 'singed'
              }
            }
          }
        ]
      }
    }
  }));
  assert.equal(result[0].signature, 'singed');

  result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            fileProperties: {
              fileName: 'test.exe',
              signature: {
                features: 'singed',
                signer: 'Apple'
              }
            }
          }
        ]
      }
    }
  }));
  assert.equal(result[0].signature, 'singed,Apple');
});
test('enrichedDllData empty dll list', function(assert) {

  const result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: []
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('enrichedDllData empty fileProperty list', function(assert) {

  const result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            name: 'test'
          }
        ]
      }
    }
  }));
  assert.equal(result.length, 1);
});

test('processList', function(assert) {

  const result = processList(Immutable.from({
    endpoint: {
      explore: {
        selectedTab: 'process'
      },
      process: {
        processTree: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1,
            childProcesses: [
              {
                id: 3,
                parentPid: 1,
                pid: 3,
                name: 'test',
                checksumSha256: 111
              },
              {
                id: 4,
                parentPid: 1,
                pid: 4,
                name: 'test',
                checksumSha256: 1
              }
            ]
          }
        ],
        processList: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1
          },
          {
            id: 3,
            parentPid: 2,
            pid: 3,
            name: 'test',
            checksumSha256: 111
          },
          {
            id: 4,
            parentPid: 2,
            pid: 4,
            name: 'test',
            checksumSha256: 1
          }
        ]

      }
    }
  }));
  assert.equal(result.length, 3);
});


test('processTree extract the matching checksum', function(assert) {

  const result = processTree(Immutable.from({
    endpoint: {
      explore: {
        selectedTab: {
          tabName: 'PROCESS',
          checksum: 111
        }
      },
      process: {
        processTree: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1122,
            childProcesses: [
              {
                id: 3,
                parentPid: 1,
                pid: 3,
                name: 'test',
                checksumSha256: 123
              },
              {
                id: 4,
                parentPid: 1,
                pid: 4,
                name: 'test',
                checksumSha256: 1222,
                childProcesses: [
                  {
                    id: 5,
                    parentPid: 4,
                    pid: 5,
                    name: 'test',
                    checksumSha256: 111
                  }
                ]
              }
            ]
          },
          {
            id: 2,
            parentPid: 0,
            pid: 2,
            name: 'test',
            checksumSha256: 2
          }
        ],
        processList: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1
          },
          {
            id: 3,
            parentPid: 2,
            pid: 3,
            name: 'test',
            checksumSha256: 113
          },
          {
            id: 4,
            parentPid: 2,
            pid: 4,
            name: 'test',
            checksumSha256: 1
          }
        ]

      }
    }
  }));

  assert.equal(result.length, 3);
});


test('isProcessLoading', function(assert) {

  let result = isProcessLoading(Immutable.from({
    endpoint: {
      process: {
        processDetailsLoading: true,
        isProcessTreeLoading: false
      }
    }
  }));
  assert.equal(result, true);

  result = isProcessLoading(Immutable.from({
    endpoint: {
      process: {
        processDetailsLoading: false,
        isProcessTreeLoading: false
      }
    }
  }));
  assert.equal(result, false);


  result = isProcessLoading(Immutable.from({
    endpoint: {
      process: {
        processDetailsLoading: false,
        isProcessTreeLoading: true
      }
    }
  }));
  assert.equal(result, true);
});
