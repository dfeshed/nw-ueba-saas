import { find, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { scaleLinear } from 'd3-scale';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | rsa-x-axis', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.scale = scaleLinear().domain([1, 3]).range([0, 400]);
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-x-axis}}`);
    assert.equal(findAll('.rsa-x-axis').length, 1, 'Testing to see if the .rsa-x-axis class exists');
  });

  test('generates axis when supplied with required attributes', async function(assert) {
    const done = assert.async();
    this.set('scale', this.scale);
    await render(hbs `{{rsa-x-axis scale=scale}}`);
    setTimeout(function() {
      assert.equal(find('.domain').getAttribute('d'), 'M0.5,6V0.5H400.5V6', 'Testing to see if the correct domain path was generated');
      assert.equal(findAll('.tick').length, '11', 'Testing to see if the correct number of tick marks were generated');
      done();
    }, 50);
  });

  test('generates proper transform when height attribute is supplied', async function(assert) {
    const done = assert.async();
    await render(hbs `{{rsa-x-axis height=200}}`);
    setTimeout(function() {
      assert.equal(find('.rsa-x-axis').getAttribute('transform'), 'translate(0,200)', 'Testing to see if the correct transform was generated');
      done();
    }, 50);
  });

  test('generates proper transform when rotation attribute is supplied', async function(assert) {
    const done = assert.async();
    this.set('scale', this.scale);
    await render(hbs `{{rsa-x-axis scale=scale rotation=-45}}`);
    setTimeout(function() {
      assert.equal(find('.tick text').getAttribute('transform'), 'rotate(-45)', 'Testing to see if the correct rotation transform was generated');
      done();
    }, 50);
  });
});
