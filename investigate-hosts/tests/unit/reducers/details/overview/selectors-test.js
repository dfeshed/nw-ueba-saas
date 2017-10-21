import { module, test } from 'qunit';
import { hostDetails } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | overview');

import {
  processHost,
  machineOsType,
  isJsonExportCompleted,
  getHostFileEntries,
  getMountedPaths
} from 'investigate-hosts/reducers/details/overview/selectors';


test('processHost', function(assert) {
  const result = processHost(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.ipAddresses[1], '10.40.15.187 / fe80::250:56ff:fe01:2bb5 | MAC Address: 00:50:56:01:2B:B5');
  assert.equal(result.loggedInUsers.length, 2);
});

test('machineOsType', function(assert) {
  const result = machineOsType(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result, 'linux');
});

test('isJsonExportCompleted', function(assert) {
  const result = isJsonExportCompleted(Immutable.from({ endpoint: { overview: { exportJSONStatus: 'completed' } } }));
  assert.equal(result, true);
});

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


