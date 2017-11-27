import { module, test } from 'qunit';
import { hostDetails } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | overview');

import {
  processHost,
  machineOsType,
  isJsonExportCompleted,
  getNetworkInterfaces } from 'investigate-hosts/reducers/details/overview/selectors';


test('processHost', function(assert) {
  const result = processHost(Immutable.from({ endpoint: { overview: { hostDetails } } }));
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

test('getNetworkInferaces', function(assert) {
  const result = getNetworkInterfaces(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result[0], '10.40.15.171 / fe80::250:56ff:fe01:4701 | MAC Address: 00:50:56:01:47:01', 'loopback ip removed');
  assert.equal(result.length, 2, 'validIPList length');
  assert.equal(result[1], '10.40.15.187,10.40.12.7 / fe80::250:56ff:fe01:2bb5,fe80::250:56ff:fe01:4701 | MAC Address: 00:50:56:01:2B:B5', 'network Interface with multiple ipv4');
});


