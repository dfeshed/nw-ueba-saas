import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  fileCount,
  hasFiles,
  fileExportLink
} from 'investigate-files/reducers/file-list/selectors';

module('Unit | selectors | file-list');

const STATE = Immutable.from({
  files: {
    fileList: {
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
      downloadId: 123
    }
  }
});

test('fileExportLink', function(assert) {
  const result = fileExportLink(STATE);
  assert.equal(result, `${location.origin}/endpoint/file/download/123`, 'should return the export link');
});

test('fileCount', function(assert) {
  const result = fileCount(STATE);
  assert.equal(result, 3, 'fileCount selector returns the file list count');
});

test('hasFiles', function(assert) {
  const result = hasFiles(STATE);
  assert.equal(result, true, 'hasFiles is true');
});