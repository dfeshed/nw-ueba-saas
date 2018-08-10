import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/color-indicator', function(hooks) {
  setupRenderingTest(hooks);

  test('renders default color indicator', async function(assert) {
    await render(hbs`{{endpoint/color-indicator}}`);
    assert.equal(findAll('.color-indicator.red').length, 1, 'Default color is red');
    assert.equal(find('.color-indicator.red circle').r.animVal.value, 6, 'Default radius is medium(6)');
  });

  test('render small orange indicator', async function(assert) {
    this.set('size', 'small');
    this.set('color', 'orange');

    await render(hbs`{{endpoint/color-indicator color=color size=size}}`);
    assert.equal(findAll('.color-indicator.orange').length, 1, 'color is orange');
    assert.equal(find('.color-indicator.orange circle').r.animVal.value, 5, 'Radius is 5');
  });


  test('axis and width parameters are set properly for custom size', async function(assert) {
    this.set('size', 20);
    this.set('color', 'green');

    await render(hbs`{{endpoint/color-indicator color=color size=size}}`);
    assert.equal(findAll('.color-indicator.green').length, 1, 'color is green');
    assert.equal(find('.color-indicator.green circle').r.animVal.value, 20, 'Radius is 20');
    assert.equal(find('.color-indicator.green circle').cx.animVal.value, 20, 'x axis is 20');
    assert.equal(find('svg.color-indicator.green').height.animVal.value, 40, 'height is 40');
  });
});
