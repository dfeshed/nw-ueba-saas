import { module, test } from 'qunit';
import { filesData } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | files');

import {
  filesWithEnrichedData,
  fileProperty,
  isAllSelected,
  selectedFileCount,
  checksums
} from 'investigate-hosts/reducers/details/files/selectors';

test('filesWithEnrichedData', function(assert) {
  const result = filesWithEnrichedData(Immutable.from({
    endpoint: {
      hostFiles: {
        files: filesData.items
      }
    }

  }));
  assert.equal(result.length, 3);
});
test('fileProperty', function(assert) {
  const result = fileProperty(Immutable.from({
    endpoint: {
      hostFiles: {
        files: filesData.items,
        selectedFileId: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33'
      }
    }
  }));
  assert.equal(result.fileName, 'systemd-journald.service');
});
test('isAllSelected', function(assert) {
  let result = isAllSelected(Immutable.from({
    endpoint: {
      hostFiles: {
        files: filesData.items,
        selectedFileId: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        selectedFileList: []
      }
    }
  }));
  assert.equal(result, false);

  result = isAllSelected(Immutable.from({
    endpoint: {
      hostFiles: {
        files: new Array(100),
        selectedFileId: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        selectedFileList: new Array(100)
      }
    }
  }));
  assert.equal(result, true);
});
test('selectedFileCount', function(assert) {
  let result = selectedFileCount(Immutable.from({
    endpoint: {
      hostFiles: {
        files: filesData.items,
        selectedFileId: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        selectedFileList: []
      }
    }
  }));
  assert.equal(result, 0);

  result = selectedFileCount(Immutable.from({
    endpoint: {
      hostFiles: {
        files: new Array(100),
        selectedFileId: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        selectedFileList: new Array(100)
      }
    }
  }));
  assert.equal(result, 100);
});
test('checksums', function(assert) {
  const result = checksums(Immutable.from({
    endpoint: {
      hostFiles: {
        files: filesData.items,
        selectedFileId: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        selectedFileList: [{ checksumSha256: 'c1' }, { checksumSha256: 'c2' } ]
      }
    }
  }));
  assert.equal(result.length, 2);
});