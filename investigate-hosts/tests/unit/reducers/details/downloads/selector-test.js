import { module, test } from 'qunit';
import {
  files,
  areFilesLoading,
  fileCount,
  hasFiles,
  isAllSelected,
  fileTotalLabel,
  nextLoadCount
} from 'investigate-hosts/reducers/details/downloads/selectors';


module('Unit | Selectors | File Context', function() {

  test('nextLoadCount more than 100', function(assert) {
    const files = {};
    for (let i = 0; i <= 102; i++) {
      files[i] = { id: i };
    }
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files
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
          downloads: {
            files
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
          downloads: {
            totalItems: 100,
            hasNext: true
          },
          filter: {
            expressionList: []
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
          downloads: {
            totalItems: 2000,
            hasNext: true
          },
          filter: {
            expressionList: [{ a: 1 }, { a: 2 }]
          }
        }
      }
    };
    const result = fileTotalLabel(state);
    assert.equal(result, '1000+');
  });

  test('isAllSelected when all are selected', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files: { a: { a: 1 }, b: { a: 2 } },
            selectedFileList: [{ a: 1 }, { a: 2 }]
          }
        }
      }
    };
    const result = isAllSelected(state);
    assert.equal(result, true);
  });

  test('isAllSelected when all are not selected', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files: { a: { a: 1 }, b: { a: 2 } },
            selectedFileList: [{ a: 2 }]
          }
        }
      }
    };
    const result = isAllSelected(state);
    assert.equal(result, false);
  });

  test('hasFiles when files are present', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files: { a: { a: 1 }, b: { a: 2 } }
          }
        }
      }
    };
    const result = hasFiles(state);
    assert.equal(result, true);
  });

  test('hasFiles when no files are present', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files: {}
          }
        }
      }
    };
    const result = hasFiles(state);
    assert.equal(result, false);
  });

  test('fileCount when files are present', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files: { a: { a: 1 }, b: { a: 2 } }
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
          downloads: {
            files: {}
          }
        }
      }
    };
    const result = fileCount(state);
    assert.equal(result, 0);
  });

  test('areFilesLoading when loading', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            areFilesLoading: 'wait'
          }
        }
      }
    };
    const result = areFilesLoading(state);
    assert.equal(result, true);
  });

  test('areFilesLoading when loading has completed', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            areFilesLoading: 'completed'
          }
        }
      }
    };
    const result = areFilesLoading(state);
    assert.equal(result, false);
  });

  test('files when files are present', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files: { a: { a: 1 }, b: { a: 2 } }
          }
        }
      }
    };
    const result = files(state);
    assert.deepEqual(result, [{ a: 1 }, { a: 2 }]);
  });

  test('files when files are not present', function(assert) {
    const state = {
      endpoint:
      {
        hostDownloads: {
          downloads: {
            files: {}
          }
        }
      }
    };
    const result = files(state);
    assert.deepEqual(result, []);
  });

});