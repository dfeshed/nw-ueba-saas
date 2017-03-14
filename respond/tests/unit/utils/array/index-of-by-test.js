import arrayIndexOfBy from 'respond/utils/array/index-of-by';
import { module, test } from 'qunit';

module('Unit | Utility | array/index of by');

const field = 'id';

const obj1 = {};
obj1[field] = 'foo';

const obj2 = {};
obj2[field] = 'bar';

const arr = [ obj1, obj2, null, { } ];


test('it works', function(assert) {
  const index = 1;
  const result = arrayIndexOfBy(arr, field, arr[index][field]);
  assert.equal(result, index);
});

test('it returns -1 given no input', function(assert) {
  const result = arrayIndexOfBy();
  assert.equal(result, -1);
});

test('it returns -1 when given empty array', function(assert) {
  const result = arrayIndexOfBy([], field, 'baz');
  assert.equal(result, -1);
});