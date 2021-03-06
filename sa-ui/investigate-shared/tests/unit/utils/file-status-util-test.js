import { module, test } from 'qunit';

import {
  checksumsWithoutRestricted,
  hasRestrictedEntry,
  isAllAreRestrictedEntry
} from 'investigate-shared/utils/file-status-util';

module('Unit | Utils | file status util', function() {

  test('checksumsWithoutRestricted', function(assert) {
    const fileList = [ { fileName: 'test', checksumSha256: '1' }, { fileName: 'xyz', checksumSha256: '1' }];
    const restrictedList = ['xyz'];
    const result = checksumsWithoutRestricted(fileList, restrictedList);
    assert.equal(result.length, 1);
    assert.equal(result[0], '1');

  });

  test('hasRestrictedEntry returns true', function(assert) {
    const fileList = [ 'test', 'xyz'];
    const restrictedList = ['xyz'];
    const result = hasRestrictedEntry(fileList, restrictedList);
    assert.equal(result, true);
  });

  test('hasRestrictedEntry returns false', function(assert) {
    const fileList = [ 'test', 'xyz'];
    const restrictedList = ['xyz123'];
    const result = hasRestrictedEntry(fileList, restrictedList);
    assert.equal(result, false);
  });


  test('isAllAreRestrictedEntry returns true if all are in the list', function(assert) {
    const fileList = [ 'test', 'xyz'];
    const restrictedList = ['xyz', 'test'];
    const result = isAllAreRestrictedEntry(fileList, restrictedList);
    assert.equal(result, true);
  });

  test('isAllAreRestrictedEntry returns false', function(assert) {
    const fileList = [ 'test', 'xyz'];
    const restrictedList = ['xyz', 'test2'];
    const result = isAllAreRestrictedEntry(fileList, restrictedList);
    assert.equal(result, false);
  });
});
