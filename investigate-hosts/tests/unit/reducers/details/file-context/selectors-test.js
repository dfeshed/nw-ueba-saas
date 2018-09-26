import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';


import {
  listOfFiles,
  isAllSelected,
  fileContextFileProperty,
  isDataLoading
} from 'investigate-hosts/reducers/details/file-context/selectors';


module('Unit | Reducers | File Context', function() {
  test('fileContext', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'Test'
            },
            2: {
              id: 2,
              fileName: 'Test'
            },
            3: {
              id: 3,
              fileName: 'Test'
            }
          }
        },
        explore: {

        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.length, 3);
  });

  test('filter drivers based on valid checksum', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              checksumSha256: 1,
              fileName: 'Test'
            },
            2: {
              id: 2,
              fileName: 'Test'
            },
            3: {
              id: 3,
              fileName: 'Test'
            }
          }
        },
        explore: {
          selectedTab: {
            tabName: 'DRIVERS',
            checksum: 1
          }
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.length, 1);
  });

  test('context sort by file name', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C'
            },
            2: {
              id: 2,
              fileName: 'Z'
            },
            3: {
              id: 3,
              fileName: 'A'
            }
          }
        },
        explore: {

        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result[0].fileName, 'A');
  });

  test('isDataLoading is set to true when contextLoadingStatus is wait', function(assert) {
    const result = isDataLoading(Immutable.from({
      endpoint: {
        drivers: {
          contextLoadingStatus: 'wait'
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.deepEqual(result, true);
  });

  test('isDataLoading is set to false when contextLoadingStatus is complete', function(assert) {
    const result = isDataLoading(Immutable.from({
      endpoint: {
        drivers: {
          contextLoadingStatus: 'complete'
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.deepEqual(result, false);
  });

  test('fileContextFileProperty when selectedRowId is empty', function(assert) {
    const result = fileContextFileProperty(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C'
            },
            2: {
              id: 2,
              fileName: 'Z'
            },
            3: {
              id: 3,
              fileName: 'A'
            }
          },
          selectedRowId: null
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.fileName, 'A', 'file name ');
  });

  test('isAllSelected if all drivers selected', function(assert) {
    const result = isAllSelected(Immutable.from({
      endpoint: {
        drivers: {
          fileContextSelections: new Array(3),
          fileContext: {
            1: {
              id: 1,
              fileName: 'C'
            },
            2: {
              id: 2,
              fileName: 'Z'
            },
            3: {
              id: 3,
              fileName: 'A'
            }
          },
          selectedRowId: null
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result, true, 'isAllSelected should true ');
  });

});

