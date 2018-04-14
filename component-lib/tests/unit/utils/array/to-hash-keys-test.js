import arrayToHashKeys from 'component-lib/utils/array/to-hash-keys';
import { module, test } from 'qunit';

module('Unit | Utility | array/to hash keys');

const arr = ['a', 'b', 3];
const notInArr = 'c';

test('it works', function(assert) {
  const result = arrayToHashKeys(arr);
  const keys = Object.keys(result);

  assert.equal(keys && keys.length, arr.length, 'Expected hash key count to match the given array size.');

  assert.ok(arr[0] in result, 'Expected hash to have a key for each array value.');
  assert.ok(arr[1] in result, 'Expected hash to have a key for each array value.');
  assert.ok(String(arr[2]) in result, 'Expected hash to have a key for each array value.');

  assert.notOk(notInArr in result, 'Expected hash to not have a key for a value not in the array value.');
});

test('it returns an empty hash when given no input', function(assert) {
  const result = arrayToHashKeys();
  const keys = Object.keys(result);
  assert.equal(typeof result, 'object', 'Expected a hash.');
  assert.equal(keys && keys.length, 0, 'Expected a hash with no keys.');
});

test('it returns an empty hash when given an empty array', function(assert) {
  const result = arrayToHashKeys([]);
  const keys = Object.keys(result);
  assert.equal(typeof result, 'object', 'Expected a hash.');
  assert.equal(keys && keys.length, 0, 'Expected a hash with no keys.');
});
