import { isArray } from '@ember/array';
import arrayFromHash from 'respond/utils/array/from-hash';
import { module, test } from 'qunit';

module('Unit | Utility | array/to hash keys');

test('it works', function(assert) {
  const hash = { foo: 1, bar: 2, baz: 3 };
  const result = arrayFromHash(hash);

  assert.ok(isArray(result), 'Expected an array result');
  assert.equal(result.length, Object.keys(hash).length, 'Expected an array member for each hash key.');

  result.forEach(({ key, value }) => {
    assert.equal(hash[key], value, 'Expected array member to match a key-value pair in the hash.');
  });
});

test('it returns an empty array when given no input', function(assert) {
  const result = arrayFromHash();
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});

test('it returns an empty array when given an empty hash', function(assert) {
  const result = arrayFromHash({});
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});
