import { module, test } from 'qunit';
import {
  listOfMftFiles,
  areMftFilesLoading,
  fileCount,
  hasMftFiles,
  isAllMftSelected,
  fileTotalLabel,
  mftSelectedFiles,
  nextLoadCount,
  pageStatus
} from 'investigate-hosts/reducers/details/mft-directory/selectors';


module('Unit | Selectors | mft-directory', function() {

  test('nextLoadCount more than 100', function(assert) {
    const files = {};
    for (let i = 0; i <= 102; i++) {
      files[i] = { id: i };
    }
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files,
                sortField: 'creation'
              }
            }
          }
        }
    };
    const result = nextLoadCount(state);
    assert.equal(result, 100);
  });

  test('nextLoadCount less than 100', function(assert) {
    const files = {};
    for (let i = 0; i < 50; i++) {
      files[i] = { id: i };
    }
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files,
                sortField: 'creation'
              }
            }
          }
        }
    };
    const result = nextLoadCount(state);
    assert.equal(result, 50);
  });

  test('fileTotalLabel when under 1000', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                totalMftItems: 100,
                sortField: 'creation'
              },
              filter: {
                expressionList: []
              }
            },
            downloads: {
              hasNext: true
            }
          }
        }
    };
    const result = fileTotalLabel(state);
    assert.equal(result, '100');
  });

  test('fileTotalLabel when under 1000', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                totalMftItems: 2000,
                hasMftNext: true
              },
              filter: {
                expressionList: [{ a: 1 }, { a: 2 }]
              }
            },
            downloads: {
              hasNext: true
            }
          }
        }
    };
    const result = fileTotalLabel(state);
    assert.equal(result, '1000+');
  });

  test('isAllMftSelected when all are selected', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: { a: { a: 1 }, b: { a: 2 } },
                selectedMftFileList: [{ a: 1 }, { a: 2 }],
                sortField: 'creation'
              }
            }
          }
        }
    };
    const result = isAllMftSelected(state);
    assert.equal(result, true);
  });
  test('mftSelectedFiles ', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: { a: { a: 1 }, b: { a: 2 } },
                selectedMftFileList: [{ a: 1 }, { a: 2 }],
                sortField: 'creation'
              }
            }
          }
        }
    };
    const result = mftSelectedFiles(state);
    assert.deepEqual(result, [{ a: 1 }, { a: 2 }]);
  });

  test('isAllMftSelected when all are not selected', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: { a: { a: 1 }, b: { a: 2 } },
                selectedMftFileList: [{ a: 2 }],
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result = isAllMftSelected(state);
    assert.equal(result, false);
  });

  test('hasMftFiles when files are present', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: { a: { a: 1 }, b: { a: 2 } },
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result = hasMftFiles(state);
    assert.equal(result, true);
  });

  test('hasMftFiles when no files are present', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: {},
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result = hasMftFiles(state);
    assert.equal(result, false);
  });

  test('fileCount when files are present', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: { a: { a: 1 }, b: { a: 2 } },
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result = fileCount(state);
    assert.equal(result, 2);
  });

  test('fileCount when no files are present', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: {},
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result = fileCount(state);
    assert.equal(result, 0);
  });

  test('areMftFilesLoading when loading', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                loading: 'wait'
              }
            },
            downloads: {
              areFilesLoading: 'wait'
            }
          }
        }
    };
    const result = areMftFilesLoading(state);
    assert.equal(result, true);
  });

  test('areMftFilesLoading when loading has completed', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                loading: 'completed'
              }
            },
            downloads: {
              areFilesLoading: 'completed'
            }
          }
        }
    };
    const result = areMftFilesLoading(state);
    assert.equal(result, false);
  });

  test('files when files are present', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: { a: { a: 1 }, b: { a: 2 } },
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result = listOfMftFiles(state);
    assert.deepEqual(result, [{ a: 1 }, { a: 2 }]);
  });

  test('files when files are not present', function(assert) {
    const state = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: {},
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result = listOfMftFiles(state);
    assert.deepEqual(result, []);
  });

  test('pageStatus selector tests', function(assert) {
    const state1 = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: {},
                sortField: 'creationDate'
              }
            }
          }
        }
    };
    const result1 = pageStatus(state1);
    const state2 = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: {},
                sortField: 'creationDate',
                loading: 'wait',
                hasMftNext: true
              }
            }
          }
        }
    };
    const result2 = pageStatus(state2);
    const state3 = {
      endpoint:
        {
          hostDownloads: {
            mft: {
              mftDirectory: {
                files: {},
                sortField: 'creationDate',
                loading: 'completed',
                hasMftNext: true
              }
            }
          }
        }
    };
    const result3 = pageStatus(state3);
    assert.deepEqual(result1, 'complete');
    assert.deepEqual(result2, 'streaming');
    assert.deepEqual(result3, 'stopped');
  });

});