import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/process-analysis/wrapper', function(hooks) {
  setupRenderingTest(hooks);

  test('process-analysis/wrapper renders', async function(assert) {

    await render(hbs`{{endpoint/process-analysis/wrapper}}`);

    assert.equal(findAll('.scrollable-panel-wrapper').length, 1, 'Process Analysis wrapper present');

    assert.equal(findAll('.process-list-box').length, 3, '3 columns present');
  });
});
