import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { selectAll } from 'd3-selection';
module('Integration | Component | endpoint/process-analysis/process-tree', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the process tree', async function(assert) {
    await render(hbs`{{endpoint/process-analysis/process-tree}}`);
    assert.equal(findAll('.process').length, 5, 'Expected to render 5 nodes');
  });

  test('it should expand the node on click', async function(assert) {
    await render(hbs`{{endpoint/process-analysis/process-tree}}`);
    await selectAll('.process:nth-of-type(2)').dispatch('click');
    assert.equal(findAll('.process').length, 7, 'Expected to render 7 nodes');
  });
});
