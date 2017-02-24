import forceLayoutLinkCoords from 'respond/utils/force-layout/link-coords';
import { module, test } from 'qunit';

module('Unit | Utility | force layout/link coords');

test('it works with a vertical link', function(assert) {
  const result = forceLayoutLinkCoords(0, 0, 5, 0, 100, 10);
  assert.equal(Math.round(Number(result.y1)), 5, 'Unexpected y1 result.');
  assert.equal(Math.round(Number(result.y2)), 90, 'Unexpected y2 result.');
});

test('it works with a horizontal link', function(assert) {
  const result = forceLayoutLinkCoords(0, 0, 5, 100, 0, 10);
  assert.equal(Math.round(Number(result.x1)), 5, 'Unexpected x1 result.');
  assert.equal(Math.round(Number(result.x2)), 90, 'Unexpected x2 result.');
});

test('it works with a diagonal link', function(assert) {
  const result = forceLayoutLinkCoords(0, 0, Math.pow(2, 0.5), 100, 100, Math.pow(2, 0.5));
  assert.equal(Math.round(Number(result.x1)), 1, 'Unexpected x1 result.');
  assert.equal(Math.round(Number(result.y1)), 1, 'Unexpected y1 result.');
  assert.equal(Math.round(Number(result.x2)), 99, 'Unexpected x2 result.');
  assert.equal(Math.round(Number(result.y2)), 99, 'Unexpected y2 result.');
});
