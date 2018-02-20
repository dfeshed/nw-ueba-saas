import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  fileCount,
  hasFiles,
  fileExportLink,
  fileCountForDisplay,
  serviceList
} from 'investigate-files/reducers/file-list/selectors';

module('Unit | selectors | file-list');

const STATE = Immutable.from({
  files: {
    filter: {
    },
    fileList: {
      totalItems: 3,
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
      downloadId: 123,
      listOfServices: []
    }
  }
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
        files: [...Array(2000)]
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
