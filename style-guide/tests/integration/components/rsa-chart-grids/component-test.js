import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import d3 from 'd3';

moduleForComponent('component:rsa-chart-grids', 'Integration | Component | rsa-chart-grids', {
  integration: true,

  beforeEach() {
    this.width = 400;
    this.height = 150;
    this.xScale = d3.scaleLinear().domain([1, 3]).range([0, this.width]);
    this.yScale = d3.scaleLinear().domain([2, 10]).range([0, this.height]);
  }
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-chart-grids}}`);
  assert.equal(this.$('.grids').length, 1, 'Testing to see if the .grids class exists');
  assert.equal(this.$('.x').length, 1, 'Testing to see if the .x class exists');
  assert.equal(this.$('.y').length, 1, 'Testing to see if the .y class exists');
});

test('generates grids when supplied with required attributes', function(assert) {
  const done = assert.async();
  this.set('xScale', this.xScale);
  this.set('yScale', this.yScale);
  this.set('width', this.width);
  this.set('height', this.height);
  this.render(hbs `{{rsa-chart-grids xScale=xScale yScale=yScale width=width height=height}}`);
  setTimeout(function() {
    assert.equal(this.$('.x .tick').length, 11, 'Testing to see if the correct number of x ticks were generated');
    assert.equal(this.$('.y .tick').length, 9, 'Testing to see if the correct number of y ticks were generated');
    done();
  }, 50);
});

test('does not generate x grid when "showXGrid" attribute is set to false', function(assert) {
  const done = assert.async();
  this.set('yScale', this.yScale);
  this.set('height', this.height);
  this.render(hbs `{{rsa-chart-grids showXGrid=false yScale=yScale height=height}}`);
  setTimeout(function() {
    assert.equal(this.$('.x').length, 0, 'Testing to see if the x grid was not generated');
    assert.equal(this.$('.y .tick').length, 9, 'Testing to see if the correct y grid was generated');
    done();
  }, 50);
});

test('does not generate y grid when "showYGrid" attribute is set to false', function(assert) {
  const done = assert.async();
  this.set('xScale', this.xScale);
  this.set('width', this.width);
  this.render(hbs `{{rsa-chart-grids showYGrid=false xScale=xScale width=width}}`);
  setTimeout(function() {
    assert.equal(this.$('.y').length, 0, 'Testing to see if the y grid was not generated');
    assert.equal(this.$('.x .tick').length, 11, 'Testing to see if the correct x was generated');
    done();
  }, 50);
});

test('generates specified number of X/Y ticks when tick count attributes are set', function(assert) {
  const done = assert.async();
  this.set('xScale', this.xScale);
  this.set('yScale', this.yScale);
  this.set('width', this.width);
  this.set('height', this.height);
  this.render(hbs `{{rsa-chart-grids xScale=xScale yScale=yScale width=width height=height xTickCount=5 yTickCount=5}}`);
  setTimeout(function() {
    assert.equal(this.$('.x .tick').length, 5, 'Testing to see if the correct number of x ticks were generated');
    assert.equal(this.$('.y .tick').length, 5, 'Testing to see if the correct number of y ticks were generated');
    done();
  }, 50);
});