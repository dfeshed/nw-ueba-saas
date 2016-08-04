import safeCallback from 'sa/utils/safe-callback';
import { module, test } from 'qunit';

module('Unit | Utility | safe callback');

test('it works', function(assert) {
  assert.expect(2);

  const fn = (() => {
    assert.ok(true, 'The given function was invoked.');
  });

  assert.ok(safeCallback, 'The utility is defined');
  safeCallback(null);
  safeCallback(fn);
});
