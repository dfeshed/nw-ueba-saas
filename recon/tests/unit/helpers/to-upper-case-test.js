
import { toUpperCase } from 'dummy/helpers/to-upper-case';
import { module, test } from 'qunit';

module('Unit | Helper | to upper case');

test('toUpperCase works', function(assert) {
  const result = toUpperCase(['foo']);
  assert.equal(result, 'FOO');
});

