import mathRadToDeg from 'respond/utils/math/rad-to-deg';
import { module, test } from 'qunit';

module('Unit | Utility | math/rad to deg');

test('it works', function(assert) {
  let result;
  result = mathRadToDeg(2 * Math.PI);
  result = parseInt(result, 10);
  assert.equal(result, 360, 'Unexpected result.');

  result = mathRadToDeg(Math.PI);
  result = parseInt(result, 10);
  assert.equal(result, 180, 'Unexpected result.');

  result = mathRadToDeg(Math.PI / 2);
  result = parseInt(result, 10);
  assert.equal(result, 90, 'Unexpected result.');

  result = mathRadToDeg(0);
  result = parseInt(result, 10);
  assert.equal(result, 0, 'Unexpected result.');

  result = mathRadToDeg(-1 * Math.PI / 2);
  result = parseInt(result, 10);
  assert.equal(result, -90, 'Unexpected result.');
});

