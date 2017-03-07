
import { checkNull } from 'dummy/helpers/check-null';
import { module, test } from 'qunit';

module('Unit | Helper | check null');

test('it works', function(assert) {
  assert.equal(checkNull([null, 'alt-text']), 'alt-text');
  assert.equal(checkNull(['actual', 'alt-text']), 'actual');
});

