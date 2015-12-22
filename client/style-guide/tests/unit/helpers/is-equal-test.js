import { isEqual } from '../../../helpers/is-equal';
import { module, test } from 'qunit';

module('Unit | Helper | is equal');

// Replace this with your real tests.
test('it works', function(assert) {
  assert.notOk(isEqual(['a', 'b']));
  assert.notOk(isEqual([1, 2]));
  assert.ok(isEqual([2, 2]));
});
