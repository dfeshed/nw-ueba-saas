import hexToInt from 'dummy/utils/hex-to-int';
import { module, test } from 'qunit';

module('Unit | Utility | hex to int');

test('hexToInt works', function(assert) {
  const result = hexToInt('00000000a');
  assert.equal(result, 10);
});
