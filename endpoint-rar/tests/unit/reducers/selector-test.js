import { module, test } from 'qunit';
import { rarInstallerURL } from 'endpoint-rar/reducers/selectors';
import Immutable from 'seamless-immutable';

module('Unit | selectors | details');

test('rarInstallerURL', function(assert) {
  let result = rarInstallerURL(Immutable.from({ rar: { downloadId: 'test_id', serverId: 'test_serverId' } }));
  assert.equal(result.includes('test_serverId'), true, 'Returns constructed URL');
  result = rarInstallerURL(Immutable.from({ rar: { downloadId: 'test_id' } }));
  assert.equal(result.includes('test_id'), true, 'Returns constructed URL');
});