import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { scaleLinear } from 'd3-scale';

moduleForComponent('/rsa-area-series', 'Integration | Component | rsa-area-series', {
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
    this.height = 150;
    this.xScale1 = scaleLinear().domain([1, 3]).range([0, 400]);
    this.yScale1 = scaleLinear().domain([4, 6]).range([150, 0]);
    this.xScale2 = scaleLinear().domain([4, 6]).range([0, 400]);
    this.yScale2 = scaleLinear().domain([7, 9]).range([150, 0]);
  }
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-area-series}}`);
  assert.equal(this.$('.rsa-area-series').length, 1, 'Testing to see if the .rsa-area-series class exists');
});

test('generates path when supplied with required attributes', function(assert) {
  const done = assert.async();
  this.set('data', this.data);
  this.set('height', this.height);
  this.set('xScale', this.xScale1);
  this.set('yScale', this.yScale1);
  this.render(hbs `{{rsa-area-series data=data height=height xScale=xScale yScale=yScale}}`);
  setTimeout(function() {
    assert.equal(this.$('.rsa-area-series').attr('d'), 'M0,150L200,75L400,0L400,150L200,150L0,150Z', 'Testing to see if the correct path was generated');
    done();
  }, 50);
});

test('generates path when supplied with multiple series of data', function(assert) {
  const done = assert.async();
  this.set('data', this.data);
  this.set('height', this.height);
  this.set('xScale', this.xScale2);
  this.set('yScale', this.yScale2);
  this.render(hbs `{{rsa-area-series data=data dataIndex=1 height=height xScale=xScale yScale=yScale}}`);
  setTimeout(function() {
    assert.equal(this.$('.rsa-area-series').attr('d'), 'M0,150L200,75L400,0L400,150L200,150L0,150Z', 'Testing to see if the correct path was generated for the second data series');
    done();
  }, 50);
});

test('generates path when supplied with custom data accessor properties', function(assert) {
  const done = assert.async();
  this.set('data', this.customData);
  this.set('height', this.height);
  this.set('xScale', this.xScale1);
  this.set('yScale', this.yScale1);
  this.set('xProp', 'foo');
  this.set('yProp', 'bar');
  this.render(hbs `{{rsa-area-series data=data height=height xProp=xProp yProp=yProp xScale=xScale yScale=yScale}}`);
  setTimeout(function() {
    assert.equal(this.$('.rsa-area-series').attr('d'), 'M0,150L200,75L400,0L400,150L200,150L0,150Z', 'Testing to see if the correct path was generated using custom x/y properties');
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
    assert.equal(this.$('.rsa-line-series').attr('d'), 'M0,-5.26429605180997L3.03934274260637,0L0,5.26429605180997L-3.03934274260637,0Z', 'Testing to see if the correct path was generated for one data point');
    done();
  }, 50);
});
