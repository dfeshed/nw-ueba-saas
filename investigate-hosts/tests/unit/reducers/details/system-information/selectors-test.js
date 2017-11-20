import { module, test } from 'qunit';
import { hostDetails } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | systemInformation');

import {
  getHostFileEntries,
  getMountedPaths,
  getWindowsPatches
} from 'investigate-hosts/reducers/details/system-information/selectors';

test('getHostFileEntries', function(assert) {
  const result = getHostFileEntries(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.length, 3);
  assert.equal(result[0].ip, '127.0.0.1');
});

test('getMountedPaths', function(assert) {
  const result = getMountedPaths(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.length, 28);
  assert.equal(result[0].fileSystem, 'rootfs');
});

test('getWindowsPatches', function(assert) {
  const result = getWindowsPatches(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.length, 6);
});

