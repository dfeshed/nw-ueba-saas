import { callMethod } from '../../../helpers/call-method';
import { module, test } from 'qunit';

module('Unit | Helper | call method');

test('it works', function(assert) {
  assert.expect(1);

  // Define a simple object with an 'echo' method that just returns whatever arguments you pass it.
  const ctxt = {
    sum(x, y) {
      return x + y;
    }
  };

  // This should call ctxt.sum(1,2), which should return return 'foo,bar'].
  let result = callMethod([ctxt, 'sum', 1, 2]);
  assert.equal(result, 3, 'Unexpected response from method invocation.');
});
