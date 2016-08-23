import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import d3 from 'd3';

moduleForComponent('/rsa-y-axis', 'Integration | Component | rsa-y-axis', {
  integration: true,

  beforeEach() {
    this.scale = d3.scaleLinear().domain([4, 6]).range([150, 0]);
  }
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-y-axis}}`);
  assert.equal(this.$('.rsa-y-axis').length, 1, 'Testing to see if the .rsa-y-axis class exists');
});

test('generates axis when supplied with required attributes', function(assert) {
  const done = assert.async();
  this.set('scale', this.scale);
  this.render(hbs `{{rsa-y-axis scale=scale}}`);
  setTimeout(function() {
    assert.equal(this.$('.domain').attr('d'), 'M-6,150.5H0.5V0.5H-6', 'Testing to see if the correct domain path was generated');
    assert.equal(this.$('.tick').length, '5', 'Testing to see if the correct number of tick marks were generated');
    done();
  }, 50);
});

test('generates proper transform when rotation attribute is supplied', function(assert) {
  const done = assert.async();
  this.set('scale', this.scale);
  this.render(hbs `{{rsa-y-axis scale=scale rotation=-45}}`);
  setTimeout(function() {
    assert.equal(this.$('.tick text').attr('transform'), 'rotate(-45)', 'Testing to see if the correct rotation transform was generated');
    done();
  }, 50);
});
