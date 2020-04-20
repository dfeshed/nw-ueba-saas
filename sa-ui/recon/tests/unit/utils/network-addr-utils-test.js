import {
  isNetworkAddress,
  getIpAddressMetaValue,
  getPortMetaValue
} from 'recon/utils/network-addr-utils';
import { module, test } from 'qunit';

module('Unit | Utility | Network Address Utils');

test('isNetworkAddress works', function(assert) {
  assert.ok(isNetworkAddress('source'), 'Expected source to be network address');
  assert.equal(isNetworkAddress('serviceid'), false, 'Did not expect serviceid to be network address');
});

test('getIpAddressMetaValue works', function(assert) {
  const { metaName, metaValue } = getIpAddressMetaValue('ip.src : port.src', '10.1.1.1 : 8080');
  assert.equal(metaName, 'ip.src', 'meta name must be extracted correctly');
  assert.equal(metaValue, '10.1.1.1', 'meta value must be extracted correctly');
});

test('getPortMetaValue works', function(assert) {
  const { metaName, metaValue } = getPortMetaValue('ip.src : port.src', '10.1.1.1 : 8080');
  assert.equal(metaName, 'port.src', 'meta name must be extracted correctly');
  assert.equal(metaValue, '8080', 'meta value must be extracted correctly');
});
