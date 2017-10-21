import { module, test } from 'qunit';
import { filesData } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | files');

import {
  filesWithEnrichedData,
  fileProperty
} from 'investigate-hosts/reducers/details/files/selectors';

test('filesWithEnrichedData', function(assert) {
  const result = filesWithEnrichedData(Immutable.from({
    endpoint: {
      hostFiles: {
        files: filesData.items,
        selectedFileHash: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33'
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
        selectedFileHash: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33'
      }
    }
  }));
  assert.equal(result.fileName, 'systemd-journald.service');
});
