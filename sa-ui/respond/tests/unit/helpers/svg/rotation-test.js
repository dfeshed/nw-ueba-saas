import { svgRotation } from 'respond/helpers/svg/rotation';
import { module, test } from 'qunit';

module('Unit | Helper | svg/rotation');

test('it works', function(assert) {
  assert.equal(svgRotation(45), 'rotate(45.0)', 'Unexpected result.');
});
