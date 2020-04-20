import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll } from '@ember/test-helpers';

module('Integration | Component | spec body category', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs`{{spec-category}}`);
    assert.ok(findAll('.spec-category').length, 'Could not find component\'s root DOM element.');
  });
});
