import hexToInt from 'dummy/utils/hex-to-int';
import { module, test } from 'qunit';

module('Unit | Utility | hex to int');

// Replace this with your real tests.
test('hexToInt works', function(assert) {
  let result = hexToInt('00000000a');
  assert.equal(result, 10);
});
