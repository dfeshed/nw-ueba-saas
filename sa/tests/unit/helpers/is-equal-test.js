import { isEqual } from '../../../helpers/is-equal';
import { module, test } from 'qunit';

module('Unit | Helper | is equal');

test('it works', function(assert) {
  assert.expect(2);
  assert.equal(isEqual(['42', 42, false]), false, 'Strict comparison failed.');
  assert.equal(isEqual(['42', 42, true]), true, 'Non-strict comparison failed.');
});
