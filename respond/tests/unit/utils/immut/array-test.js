import { isArray } from '@ember/array';
import * as ArrayUtils from 'respond/utils/immut/array';
import { module, test } from 'qunit';

module('Unit | Utility | immut/array');

const arr1 = ['c', 'd', 'e'];
const remove1 = 'd';
const removeIndex1 = 1;
const push1 = 'f';

test('push works', function(assert) {
  const push1 = 'foo';
  const result = ArrayUtils.push(arr1, push1);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.notEqual(arr1, result, 'Expected a new array object.');
  assert.equal(result.length, arr1.length + 1, 'Expected result to include all child array members.');

  assert.equal(result[0], arr1[0], 'Expected result to contain the input values in sequence.');
  assert.equal(result[1], arr1[1], 'Expected result to contain the input values in sequence.');
  assert.equal(result[2], arr1[2], 'Expected result to contain the input values in sequence.');
  assert.equal(result[3], push1, 'Expected result to contain the input values in sequence.');
});

test('removeAt works', function(assert) {
  const result = ArrayUtils.removeAt(arr1, removeIndex1);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.notEqual(arr1, result, 'Expected a new array object.');
  assert.equal(result.length, arr1.length - 1, 'Expected result to exclude given item.');

  assert.equal(result[0], arr1[0], 'Expected result to contain the input values in sequence.');
  assert.equal(result[1], arr1[2], 'Expected result to contain the input values in sequence.');
});

test('remove works', function(assert) {
  const result = ArrayUtils.remove(arr1, remove1);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.notEqual(arr1, result, 'Expected a new array object.');
  assert.equal(result.length, arr1.length - 1, 'Expected result to exclude given item.');

  assert.equal(result[0], arr1[0], 'Expected result to contain the input values in sequence.');
  assert.equal(result[1], arr1[2], 'Expected result to contain the input values in sequence.');
});

test('toggle removes when expected', function(assert) {
  const result = ArrayUtils.toggle(arr1, remove1);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.notEqual(arr1, result, 'Expected a new array object.');
  assert.equal(result.length, arr1.length - 1, 'Expected result to exclude given item.');

  assert.equal(result[0], arr1[0], 'Expected result to contain the input values in sequence.');
  assert.equal(result[1], arr1[2], 'Expected result to contain the input values in sequence.');
});

test('toggle pushes when expected', function(assert) {
  const result = ArrayUtils.toggle(arr1, push1);
  assert.ok(isArray(result), 'Expected an array result.');
  assert.notEqual(arr1, result, 'Expected a new array object.');
  assert.equal(result.length, arr1.length + 1, 'Expected result to include given item.');

  assert.equal(result[0], arr1[0], 'Expected result to contain the input values in sequence.');
  assert.equal(result[1], arr1[1], 'Expected result to contain the input values in sequence.');
  assert.equal(result[2], arr1[2], 'Expected result to contain the input values in sequence.');
  assert.equal(result[3], push1, 'Expected result to contain the input values in sequence.');
});
