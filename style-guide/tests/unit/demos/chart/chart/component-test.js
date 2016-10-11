import { moduleFor, test } from 'ember-qunit';
import { scaleLinear } from 'd3-scale';

moduleFor('component:rsa-chart', 'Unit | rsa-chart', {
  unit: true
});

const delta = 20;
const zeroMargin = { top: 0, bottom: 0, left: 0, right: 0 };
const deltaMargin = { top: delta, bottom: delta, left: delta, right: delta };

test('should correctly compute graph width', function(assert) {
  const chart = this.subject();

  chart.set('margin', zeroMargin);
  const initial = chart.get('graphWidth');

  chart.set('margin', deltaMargin);
  assert.equal(chart.get('graphWidth'), initial - delta * 2, 'Setting margin');
});

test('should correctly compute graph height', function(assert) {
  const chart = this.subject();

  chart.set('margin', zeroMargin);
  const initial = chart.get('graphHeight');

  chart.set('margin', deltaMargin);
  assert.equal(chart.get('graphHeight'), initial - delta * 2, 'Setting margin');
});

test('should correctly compute domain', function(assert) {
  const chart = this.subject();
  const dataWithDefaultProperties = [[
    { x: 1, y: 4 },
    { x: 2, y: 5 },
    { x: 3, y: 6 }
  ]];
  const multiDataWithDefaultProperties = [
    [
      { x: 1, y: 4 },
      { x: 2, y: 5 },
      { x: 3, y: 6 }
    ],
    [
      { x: 4, y: 7 },
      { x: 5, y: 8 },
      { x: 6, y: 9 }
    ]
  ];
  const dataWithCustomProperties = [[
    { foo: 1, bar: 4 },
    { foo: 2, bar: 5 },
    { foo: 3, bar: 6 }
  ]];
  const multiDataWithCustomProperties = [
    [
      { foo: 1, bar: 4 },
      { foo: 2, bar: 5 },
      { foo: 3, bar: 6 }
    ],
    [
      { foo: 4, bar: 7 },
      { foo: 5, bar: 8 },
      { foo: 6, bar: 9 }
    ]
  ];

  chart.set('data', dataWithDefaultProperties);
  chart.set('xAxisStartsAtZero', true);
  assert.deepEqual(chart.get('xDomain'), [0, 3], 'xDomain starts at 0');
  chart.set('xAxisStartsAtZero', false);
  assert.deepEqual(chart.get('xDomain'), [1, 3], 'xDomain starts at minimum data extent');

  chart.set('yAxisStartsAtZero', false);
  assert.deepEqual(chart.get('yDomain'), [4, 6], 'yDomain starts at minimum data extent');
  chart.set('yAxisStartsAtZero', true);
  assert.deepEqual(chart.get('yDomain'), [0, 6], 'yDomain starts at 0');

  assert.deepEqual(chart.get('xDomain'), [1, 3], 'xDomain, single series, using default xProp');
  assert.deepEqual(chart.get('yDomain'), [0, 6], 'yDomain, single series, using default xProp');

  chart.set('data', multiDataWithDefaultProperties);
  assert.deepEqual(chart.get('xDomain'), [1, 6], 'xDomain, multi series, using default xProp');
  assert.deepEqual(chart.get('yDomain'), [0, 9], 'yDomain, multi series, using default xProp');

  chart.set('xProp', 'foo');
  chart.set('yProp', 'bar');
  chart.set('data', dataWithCustomProperties);
  assert.deepEqual(chart.get('xDomain'), [1, 3], 'xDomain, single series, using custom xProp');
  assert.deepEqual(chart.get('yDomain'), [0, 6], 'yDomain, single series, using custom xProp');

  chart.set('data', multiDataWithCustomProperties);
  assert.deepEqual(chart.get('xDomain'), [1, 6], 'xDomain, multi series, using custom xProp');
  assert.deepEqual(chart.get('yDomain'), [0, 9], 'yDomain, multi series, using custom xProp');

});

test('should correctly compute range', function(assert) {
  const chart = this.subject();
  const margin = { top: 0, bottom: 0, left: 0, right: 0 };

  assert.deepEqual(chart.get('xRange'), [0, 570], 'xRange using default width');
  assert.deepEqual(chart.get('yRange'), [115, 0], 'yRange using default height');

  chart.set('chartWidth', 800);
  chart.set('chartHeight', 200);
  assert.deepEqual(chart.get('xRange'), [0, 770], 'xRange using custom width');
  assert.deepEqual(chart.get('yRange'), [165, 0], 'yRange using custom height');

  chart.set('margin', margin);
  assert.deepEqual(chart.get('xRange'), [0, 800], 'xRange using custom width and margin');
  assert.deepEqual(chart.get('yRange'), [200, 0], 'yRange using custom height and margin');
});

test('should correctly construct a scale function', function(assert) {
  const chart = this.subject();
  chart.set('xScaleFn', scaleLinear);
  chart.set('xDomain', [0, 1]);
  chart.set('xRange', [0, 10]);
  const scale = chart.get('xScale');
  assert.equal(scale(0.5), 5, 'Linear interpolation was computed properly');
});
