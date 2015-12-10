import { whichQuartile } from '../../../helpers/which-quartile';
import { module, test } from 'qunit';

module('Unit | Helper | which quartile');

test('it works', function(assert) {
  assert.expect(5);

  assert.equal(whichQuartile([-1]), 0, 'Unexpected result with negative out of bounds numerical input.');

  assert.equal(whichQuartile([12]), 0, 'Unexpected result with numerical input.');

  assert.equal(whichQuartile([88]), 3, 'Unexpected result with numerical input.');

  assert.equal(whichQuartile([101]), 3, 'Unexpected result with high out of bounds numerical input.');

  assert.equal(whichQuartile(['a']), 0, 'Unexpected result with non-numerical input.');
});
