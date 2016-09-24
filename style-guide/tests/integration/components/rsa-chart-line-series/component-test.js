import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import d3 from 'd3';

moduleForComponent('/rsa-line-series', 'Integration | Component | rsa-line-series', {
  integration: true,

  beforeEach() {
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
    this.xScale1 = d3.scaleLinear().domain([1, 3]).range([0, 400]);
    this.yScale1 = d3.scaleLinear().domain([4, 6]).range([150, 0]);
    this.xScale2 = d3.scaleLinear().domain([4, 6]).range([0, 400]);
    this.yScale2 = d3.scaleLinear().domain([7, 9]).range([150, 0]);
  }
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-line-series}}`);
  assert.equal(this.$('.rsa-line-series').length, 1, 'Testing to see if the .rsa-line-series class exists');
});

test('generates path when supplied with required attributes', function(assert) {
  const done = assert.async();
  this.set('data', this.data);
  this.set('xScale', this.xScale1);
  this.set('yScale', this.yScale1);
  this.render(hbs `{{rsa-line-series data=data xScale=xScale yScale=yScale}}`);
  setTimeout(function() {
    assert.equal(this.$('.rsa-line-series').attr('d'), 'M0,150L200,75L400,0', 'Testing to see if the correct path was generated');
    done();
  }, 50);
});

test('generates path when supplied with multiple series of data', function(assert) {
  const done = assert.async();
  this.set('data', this.data);
  this.set('xScale', this.xScale2);
  this.set('yScale', this.yScale2);
  this.render(hbs `{{rsa-line-series data=data dataIndex=1 xScale=xScale yScale=yScale}}`);
  setTimeout(function() {
    assert.equal(this.$('.rsa-line-series').attr('d'), 'M0,150L200,75L400,0', 'Testing to see if the correct path was generated for the second data series');
    done();
  }, 50);
});

test('generates path when supplied with custom data accessor properties', function(assert) {
  const done = assert.async();
  this.set('data', this.customData);
  this.set('xScale', this.xScale1);
  this.set('yScale', this.yScale1);
  this.set('xProp', 'foo');
  this.set('yProp', 'bar');
  this.render(hbs `{{rsa-line-series data=data xProp=xProp yProp=yProp xScale=xScale yScale=yScale}}`);
  setTimeout(function() {
    assert.equal(this.$('.rsa-line-series').attr('d'), 'M0,150L200,75L400,0', 'Testing to see if the correct path was generated using custom x/y properties');
    done();
  }, 50);
});

test('Symbol is drawn when there is only one data point', function(assert) {
  const done = assert.async();
  this.set('data', [[{ x: 1, y: 4 }]]);
  this.set('xScale', this.xScale1);
  this.set('yScale', this.yScale1);
  this.render(hbs `{{rsa-line-series data=data xScale=xScale yScale=yScale}}`);
  setTimeout(function() {
    // The path below relies on a diamond symbol using the default size
    assert.equal(this.$('.rsa-line-series').attr('d'), 'M0,-7.444838872816797L4.298279727294168,0L0,7.444838872816797L-4.298279727294168,0Z', 'Testing to see if the correct path was generated for one data point');
    done();
  }, 50);
});