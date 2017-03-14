import arrayFindByList from 'respond/utils/array/find-by-list';
import { module, test } from 'qunit';

module('Unit | Utility | array/find by list');

const field = 'id';
const value1 = 'foo';
const value2 = 'bar';
const value3 = 'baz';
const value4 = 'bat';

const obj1 = {};
obj1[field] = value1;

const obj2 = {};
obj2[field] = value2;

const obj3 = {};
obj3[field] = value3;

const arr = [ obj1, obj2, null, { }, obj3 ];

test('it works', function(assert) {
  const result = arrayFindByList(arr, field, [value2, value3]);
  assert.equal(result, obj2);

  const result2 = arrayFindByList(arr, field, [value4]);
  assert.notOk(result2);
});

test('it returns null given no input', function(assert) {
  const result = arrayFindByList();
  assert.notOk(result);
});

test('it returns null when given empty array', function(assert) {
  const result = arrayFindByList([], field, [value1]);
  assert.notOk(result);
});