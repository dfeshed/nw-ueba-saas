import { isArray } from '@ember/array';
import arrayFilterByList from 'respond-shared/utils/array/filter-by-list';
import { module, test } from 'qunit';

module('Unit | Utility | array/filter by list');

const first = { id: 1, nested: { other: 'x' } };
const second = { id: 2, nested: { other: 'y' } };
const third = { id: 3, nested: { other: 'z' } };
const parent = [ first, second, third ];

test('returns a single item in the array when only 1 match found', function(assert) {
  const result = arrayFilterByList(parent, 'id', [2]);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 1, 'Expected an array with single entry.');
  assert.deepEqual(result, [ second ]);
});

test('returns multiple items in the array when > 1 match found', function(assert) {
  const result = arrayFilterByList(parent, 'id', [1, 2]);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 2, 'Expected an array with 2 entries.');
  assert.deepEqual(result, [
    first,
    second
  ]);
});

test('it returns an empty array when given no input', function(assert) {
  const result = arrayFilterByList();
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});

test('it returns an empty array when given empty array', function(assert) {
  const result = arrayFilterByList([]);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});

test('it returns an empty array when given ids match that is invalid', function(assert) {
  const result = arrayFilterByList(parent, 'id', [9, 8, 7]);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});

test('it returns an empty array when given field is not present in the object(s)', function(assert) {
  const result = arrayFilterByList(parent, 'guid', [1, 2]);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});
