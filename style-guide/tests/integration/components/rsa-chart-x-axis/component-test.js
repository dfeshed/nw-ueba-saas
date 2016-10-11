import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { scaleLinear } from 'd3-scale';

moduleForComponent('/rsa-x-axis', 'Integration | Component | rsa-x-axis', {
  integration: true,

  beforeEach() {
    this.scale = scaleLinear().domain([1, 3]).range([0, 400]);
  }
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-x-axis}}`);
  assert.equal(this.$('.rsa-x-axis').length, 1, 'Testing to see if the .rsa-x-axis class exists');
});

test('generates axis when supplied with required attributes', function(assert) {
  const done = assert.async();
  this.set('scale', this.scale);
  this.render(hbs `{{rsa-x-axis scale=scale}}`);
  setTimeout(function() {
    assert.equal(this.$('.domain').attr('d'), 'M0.5,6V0.5H400.5V6', 'Testing to see if the correct domain path was generated');
    assert.equal(this.$('.tick').length, '11', 'Testing to see if the correct number of tick marks were generated');
    done();
  }, 50);
});

test('generates proper transform when height attribute is supplied', function(assert) {
  const done = assert.async();
  this.render(hbs `{{rsa-x-axis height=200}}`);
  setTimeout(function() {
    assert.equal(this.$('.rsa-x-axis').attr('transform'), 'translate(0,200)', 'Testing to see if the correct transform was generated');
    done();
  }, 50);
});

test('generates proper transform when rotation attribute is supplied', function(assert) {
  const done = assert.async();
  this.set('scale', this.scale);
  this.render(hbs `{{rsa-x-axis scale=scale rotation=-45}}`);
  setTimeout(function() {
    assert.equal(this.$('.tick text').attr('transform'), 'rotate(-45)', 'Testing to see if the correct rotation transform was generated');
    done();
  }, 50);
});
