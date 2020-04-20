import { pivotToEndpointUrl } from 'context/utils/context-data-modifier';
import { module, test } from 'qunit';

module('Unit | Utility | pivot to pivotToEndpointUrl url');

test('test for FILE_NAME query have investigate/files', function(assert) {
  const entityType = 'FILE_NAME';
  const entityId = 'naPolicyManager.dll';
  const result = pivotToEndpointUrl(entityType, entityId);
  const expectedResult = '/investigate/files?query=filename = naPolicyManager.dll';

  assert.equal(decodeURIComponent(result), expectedResult);
});


test('test for FILE_HASH query have investigate/files', function(assert) {
  const entityType = 'FILE_HASH';
  const entityId = 'naPolicyManager.dll';
  const result = pivotToEndpointUrl(entityType, entityId);
  const expectedResult = '/investigate/files?query=checksum = naPolicyManager.dll';

  assert.equal(decodeURIComponent(result), expectedResult);
});


test('test for HOST query have investigate/hosts', function(assert) {
  const entityType = 'HOST';
  const entityId = 'agent1';
  const result = pivotToEndpointUrl(entityType, entityId);
  const expectedResult = '/investigate/hosts?query=alias.host = agent1';

  assert.equal(decodeURIComponent(result), expectedResult);
});

test('test for IP query have investigate/hosts', function(assert) {
  const entityType = 'IP';
  const entityId = '10.10.10.10';
  const result = pivotToEndpointUrl(entityType, entityId);
  const expectedResult = '/investigate/hosts?query=alias.ip = 10.10.10.10';

  assert.equal(decodeURIComponent(result), expectedResult);
});

test('test for MAC_ADDRESS query have investigate/hosts', function(assert) {
  const entityType = 'MAC_ADDRESS';
  const entityId = '10.10.10.10';
  const result = pivotToEndpointUrl(entityType, entityId);
  const expectedResult = '/investigate/hosts?query=alias.mac = 10.10.10.10';

  assert.equal(decodeURIComponent(result), expectedResult);
});
