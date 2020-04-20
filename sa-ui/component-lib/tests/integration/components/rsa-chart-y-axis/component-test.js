import { find, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { scaleLinear } from 'd3-scale';

module('Integration | Component | rsa-y-axis', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.scale = scaleLinear().domain([4, 6]).range([150, 0]);
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-y-axis}}`);
    assert.equal(findAll('.rsa-y-axis').length, 1, 'Testing to see if the .rsa-y-axis class exists');
  });

  test('generates axis when supplied with required attributes', async function(assert) {
    const done = assert.async();
    this.set('scale', this.scale);
    await render(hbs `{{rsa-y-axis scale=scale}}`);
    setTimeout(function() {
      assert.equal(find('.domain').getAttribute('d'), 'M-6,150.5H0.5V0.5H-6', 'Testing to see if the correct domain path was generated');
      assert.equal(findAll('.tick').length, '11', 'Testing to see if the correct number of tick marks were generated');
      done();
    }, 50);
  });

  test('generates proper transform when rotation attribute is supplied', async function(assert) {
    const done = assert.async();
    this.set('scale', this.scale);
    await render(hbs `{{rsa-y-axis scale=scale rotation=-45}}`);
    setTimeout(function() {
      assert.equal(find('.tick text').getAttribute('transform'), 'rotate(-45)', 'Testing to see if the correct rotation transform was generated');
      done();
    }, 50);
  });
});
