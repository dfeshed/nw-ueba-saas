import { find, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-gauge', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-gauge}}`);
    assert.ok(find('svg'));
  });

  test('it does not rotate for a value of 0', async function(assert) {
    await render(hbs `{{rsa-gauge value=0}}`);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(0)');
  });

  test('it rotates halfway for a value of 0.5', async function(assert) {
    await render(hbs `{{rsa-gauge value=0.5}}`);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(135)');
  });

  test('it rotates all the way for a value of 1', async function(assert) {
    await render(hbs `{{rsa-gauge value=1}}`);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(270)');
  });

  test('it animates over time by default', async function(assert) {
    const done = assert.async(1);
    this.set('value', 0);
    await render(hbs `{{rsa-gauge value=value}}`);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(0)');
    this.set('value', 1);
    assert.notEqual(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(270)');
    setTimeout(() => {
      assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(270)');
      done();
    }, 600);
    // The animation is 500ms long, but timers can be inexact, so add 100ms to be safe.
  });

  test('it rotates instantly when animate is false', async function(assert) {
    this.set('value', 0);
    await render(hbs `{{rsa-gauge value=value animate=false}}`);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(0)');
    this.set('value', 1);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(270)');
  });

  test('it accepts a percentage string in value', async function(assert) {
    await render(hbs `{{rsa-gauge value="50%" animate=false}}`);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(135)');
  });

  test('it shows a percentage text by default', async function(assert) {
    await render(hbs `{{rsa-gauge value=1}}`);
    assert.equal(find('.gauge-value-text').textContent.trim(), '100.0%');
  });

  test('it displays the label passed to it', async function(assert) {
    await render(hbs `{{rsa-gauge value=1 label="Foo"}}`);
    assert.equal(find('.gauge-label-text').textContent.trim(), 'Foo');
  });

  test('it does not display value text if showValue is false', async function(assert) {
    await render(hbs `{{rsa-gauge value=1 showValue=false}}`);
    assert.notOk(find('.gauge-value-text'));
  });

  test('it uses custom display text if passed', async function(assert) {
    await render(hbs `{{rsa-gauge value=0.5 display="50 MB/s"}}`);
    assert.equal(find('.gauge-value-text').textContent.trim(), '50 MB/s');
  });

  test('it produces the correct number of ticks', async function(assert) {
    await render(hbs `{{rsa-gauge value=0.5}}`);
    assert.equal(findAll('.gauge-ticks').length, 6);
  });

  test('the number of ticks can be changed', async function(assert) {
    await render(hbs `{{rsa-gauge value=0.5 numTicks=10}}`);
    assert.equal(findAll('.gauge-ticks').length, 10);
  });

  test('start/end angles can be changed', async function(assert) {
    this.setProperties({
      a: Math.PI * (-1 / 2),
      b: Math.PI * (1 / 2)
    });
    await render(hbs `{{rsa-gauge value=0.5 arcStartAngle=a arcEndAngle=b}}`);
    assert.equal(find('.gauge-needle-body').getAttribute('transform'), 'translate(100,100) rotate(90)');
    // This is the path data for a half arc generated by the component
    assert.equal(find('.gauge-arc').getAttribute('d'), 'M-80,-9.797174393178826e-15A80,80,0,1,1,80,0L76,0A76,76,0,1,0,-76,-9.307315673519884e-15Z');
  });
});