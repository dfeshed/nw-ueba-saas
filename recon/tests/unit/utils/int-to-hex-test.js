import intToHex from 'dummy/utils/int-to-hex';
import { module, test } from 'qunit';

module('Unit | Utility | int to hex');

test('intToHex works', function(assert) {
  const result = intToHex(10, 16);
  assert.equal(result, '00000000a');
});
