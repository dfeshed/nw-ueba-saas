import intToHex from 'dummy/utils/int-to-hex';
import { module, test } from 'qunit';

module('Unit | Utility | int to hex');


test('intToHex works', function(assert) {
  let result = intToHex(10, 16);
  assert.equal(result, '00000000a');
});
