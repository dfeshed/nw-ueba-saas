import { arrayLookup } from 'component-lib/helpers/array-lookup';
import { module, test } from 'qunit';

module('Unit | Helper | array lookup');

test('it works', function(assert) {
  const arr = ['foo', 'bar'];

  assert.expect(arr.length);
  arr.forEach((val, index) => {
    assert.equal(
      arrayLookup([ arr, index ]),
      val,
      'Expected array member value'
    );
  });
});
