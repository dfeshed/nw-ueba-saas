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

test('it adds a 180deg flip for upside down text on a  link', function(assert) {
  // Feed it the start & end coords for a horizontal link from left to right.
  // This should not produce upside down text, so we only expect one SVG rotation.
  const result1 = forceLayoutLinkCoords(0, 0, 5, 100, 10, 5);
  const found1 = result1.textTransform.match(/rotate/g);
  assert.equal(found1.length, 1, 'Expected to find only 1 rotation when text is not upside down');

  // Feed it the start & end coords for a horizontal link from right to left.
  // This should produce upside down text, so we expect an extra SVG rotation to flip it right side up.
  const result2 = forceLayoutLinkCoords(0, 0, 5, -100, 10, 5);
  const found2 = result2.textTransform.match(/rotate/g);
  assert.equal(found2.length, 2, 'Expected to find an extra rotation to flip upside down text');
});

test('it subtracts an arrow width, if given, from the link endpoint', function(assert) {
  // Feed it a horiz link with no arrow width, and them the same link with an arrow width.
  const arrowWidth = 25;
  const result1 = forceLayoutLinkCoords(0, 0, 5, 100, 0, 5);
  const result2 = forceLayoutLinkCoords(0, 0, 5, 100, 0, 5, arrowWidth);
  assert.equal(result1.x2 - result2.x2, arrowWidth);
});