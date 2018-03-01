import { isArray } from '@ember/array';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import { module, test } from 'qunit';

module('Unit | Utility | array/to hash keys');

const arr1 = ['a', 'b'];
const arr2 = ['c', 'd', 'e'];
const parentArr = [
  { foo: { bar: arr1 } },
  { foo: { bar: arr2 } }
];


test('it works', function(assert) {
  const result = arrayFlattenBy(parentArr, 'foo.bar');
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, arr1.length + arr2.length, 'Expected result to include all child array members.');

  assert.equal(result[0], arr1[0], 'Expected result to contain the input values in sequence.');
  assert.equal(result[1], arr1[1], 'Expected result to contain the input values in sequence.');
  assert.equal(result[2], arr2[0], 'Expected result to contain the input values in sequence.');
  assert.equal(result[3], arr2[1], 'Expected result to contain the input values in sequence.');
  assert.equal(result[4], arr2[2], 'Expected result to contain the input values in sequence.');
});

test('it returns an empty array when given no input', function(assert) {
  const result = arrayFlattenBy();
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});

test('it returns an empty array when given empty array', function(assert) {
  const result = arrayFlattenBy([]);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.equal(result.length, 0, 'Expected an empty array result.');
});