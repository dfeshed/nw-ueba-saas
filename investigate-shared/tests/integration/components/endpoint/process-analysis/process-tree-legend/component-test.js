import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/process-analysis/process-tree-legend', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders static legends', async function(assert) {
    await render(hbs`{{endpoint/process-analysis/process-tree-legend}}`);
    assert.equal(findAll('.legend').length, 4, 'Expected to render 4 legends');
  });
});
