import { svgTranslation } from 'respond/helpers/svg/translation';
import { module, test } from 'qunit';

module('Unit | Helper | svg/translation');

test('it works', function(assert) {
  assert.equal(svgTranslation(10, 20), 'translate(10.0 20.0)', 'Unexpected result.');
});
