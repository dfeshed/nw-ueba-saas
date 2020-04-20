import { find, findAll, render } from '@ember/test-helpers';
import { run } from '@ember/runloop';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { scaleLinear } from 'd3-scale';

module('Integration | Component | rsa-line-series', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.data = [
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
    this.customData = [[
      { foo: 1, bar: 4 },
      { foo: 2, bar: 5 },
      { foo: 3, bar: 6 }
    ]];
    this.xScale1 = scaleLinear().domain([1, 3]).range([0, 400]);
    this.yScale1 = scaleLinear().domain([4, 6]).range([150, 0]);
    this.xScale2 = scaleLinear().domain([4, 6]).range([0, 400]);
    this.yScale2 = scaleLinear().domain([7, 9]).range([150, 0]);
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-line-series}}`);
    assert.equal(findAll('.rsa-line-series').length, 1, 'Testing to see if the .rsa-line-series class exists');
  });

  test('generates path when supplied with required attributes', async function(assert) {
    const done = assert.async();
    this.set('data', this.data);
    this.set('xScale', this.xScale1);
    this.set('yScale', this.yScale1);
    await render(hbs `{{rsa-line-series data=data xScale=xScale yScale=yScale}}`);
    run.later(this, function() {
      assert.equal(find('.rsa-line-series').getAttribute('d'), 'M0,150L200,75L400,0', 'Testing to see if the correct path was generated');
      done();
    }, 50);
  });

  test('generates path when supplied with multiple series of data', async function(assert) {
    const done = assert.async();
    this.set('data', this.data);
    this.set('xScale', this.xScale2);
    this.set('yScale', this.yScale2);
    await render(hbs `{{rsa-line-series data=data dataIndex=1 xScale=xScale yScale=yScale}}`);
    run.later(this, function() {
      assert.equal(find('.rsa-line-series').getAttribute('d'), 'M0,150L200,75L400,0', 'Testing to see if the correct path was generated for the second data series');
      done();
    }, 50);
  });

  test('generates path when supplied with custom data accessor properties', async function(assert) {
    const done = assert.async();
    this.set('data', this.customData);
    this.set('xScale', this.xScale1);
    this.set('yScale', this.yScale1);
    this.set('xProp', 'foo');
    this.set('yProp', 'bar');
    await render(hbs `{{rsa-line-series data=data xProp=xProp yProp=yProp xScale=xScale yScale=yScale}}`);
    run.later(this, function() {
      assert.equal(find('.rsa-line-series').getAttribute('d'), 'M0,150L200,75L400,0', 'Testing to see if the correct path was generated using custom x/y properties');
      done();
    }, 50);
  });

  test('Symbol is drawn when there is only one data point', async function(assert) {
    const done = assert.async();
    this.set('data', [[{ x: 1, y: 4 }]]);
    this.set('xScale', this.xScale1);
    this.set('yScale', this.yScale1);
    await render(hbs `{{rsa-line-series data=data xScale=xScale yScale=yScale}}`);
    run.later(this, function() {
      // The path below relies on a diamond symbol using the default size
      assert.equal(find('.rsa-line-series').getAttribute('d'), 'M0,-5.26429605180997L3.03934274260637,0L0,5.26429605180997L-3.03934274260637,0Z', 'Testing to see if the correct path was generated for one data point');
      done();
    }, 50);
  });

  test('Symbol is drawn/removed when adding one data point, then multiple data points', async function(assert) {
    const done = assert.async();
    this.set('data', [[{ x: 1, y: 4 }]]);
    this.set('xScale', this.xScale1);
    this.set('yScale', this.yScale1);
    await render(hbs `{{rsa-line-series data=data xScale=xScale yScale=yScale}}`);
    run.later(this, function() {
      assert.equal(findAll('.symbol').length, 1, 'Testing to see if a diamond was generated for one data point');
      this.set('data', [[
        { x: 1, y: 4 },
        { x: 2, y: 5 },
        { x: 3, y: 6 }
      ]]);
      run.later(this, function() {
        assert.equal(findAll('.symbol').length, 0, 'Testing to see if the diamond class was removed when more data was added');
        done();
      }, 50);
    }, 50);
  });
});