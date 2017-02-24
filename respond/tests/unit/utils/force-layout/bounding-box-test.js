import forceLayoutBoundingBox from 'respond/utils/force-layout/bounding-box';
import { module, test } from 'qunit';

module('Unit | Utility | force layout/bounding box');

test('it uses given defaults when no nodes are given', function(assert) {
  const result = forceLayoutBoundingBox(null, 0, 1000, 800);
  assert.equal(result.top, 0, 'Unexpected right result.');
  assert.equal(result.bottom, 800, 'Unexpected bottom result.');
  assert.equal(result.left, 0, 'Unexpected right result.');
  assert.equal(result.right, 1000, 'Unexpected right result.');
  assert.equal(result.center, 500, 'Unexpected right result.');
  assert.equal(result.middle, 400, 'Unexpected right result.');
});

test('it works with non-hidden nodes', function(assert) {
  const maxStrokeWidth = 1;
  const node1 = { x: 100, y: 100, r: 10 };
  const node2 = { x: 130, y: -50, r: 20 };
  const node3 = { x: 150, y: 50, r: 30 };
  const nodes = [ node1, node2, node3 ];

  const result = forceLayoutBoundingBox(nodes, maxStrokeWidth);

  assert.equal(result.top, node2.y - node2.r - maxStrokeWidth, 'Unexpected top result.');
  assert.equal(result.bottom, node1.y + node1.r + maxStrokeWidth, 'Unexpected bottom result.');
  assert.equal(result.left, node1.x - node1.r - maxStrokeWidth, 'Unexpected left result.');
  assert.equal(result.right, node3.x + node3.r + maxStrokeWidth, 'Unexpected right result.');
});

test('it ignores hidden nodes', function(assert) {
  const maxStrokeWidth = 1;
  const node1 = { x: 100, y: 100, r: 10, isHidden: true };
  const node2 = { x: 130, y: -50, r: 20 };
  const node3 = { x: 150, y: 50, r: 30, isHidden: true };
  const nodes = [ node1, node2, node3 ];

  const result = forceLayoutBoundingBox(nodes, maxStrokeWidth);

  assert.equal(result.top, node2.y - node2.r - maxStrokeWidth, 'Unexpected top result.');
  assert.equal(result.bottom, node2.y + node2.r + maxStrokeWidth, 'Unexpected bottom result.');
  assert.equal(result.left, node2.x - node2.r - maxStrokeWidth, 'Unexpected left result.');
  assert.equal(result.right, node2.x + node2.r + maxStrokeWidth, 'Unexpected right result.');
});

test('it uses the given defaults when all given nodes are hidden', function(assert) {
  const maxStrokeWidth = 1;
  const defaultWidth = 1000;
  const defaultHeight = 2000;
  const node1 = { x: 100, y: 100, r: 10, isHidden: true };
  const node2 = { x: 130, y: -50, r: 20, isHidden: true };
  const node3 = { x: 150, y: 50, r: 30, isHidden: true };
  const nodes = [ node1, node2, node3 ];

  const result = forceLayoutBoundingBox(nodes, maxStrokeWidth, defaultWidth, defaultHeight);

  assert.equal(result.top, 0, 'Unexpected top result.');
  assert.equal(result.bottom, defaultHeight, 'Unexpected bottom result.');
  assert.equal(result.left, 0, 'Unexpected left result.');
  assert.equal(result.right, defaultWidth, 'Unexpected right result.');
});
