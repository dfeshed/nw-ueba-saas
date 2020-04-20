import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll } from '@ember/test-helpers';

module('Integration | Component | app nav', function(hooks) {

  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +
    await render(hbs`{{app-nav}}`);
    assert.ok(findAll('.app-nav').length, 'Could not find component\'s root DOM element.');
  });

  test('has visual tour link', async function(assert) {
    await render(hbs`{{app-nav}}`);
    assert.ok(findAll('.visual-tour-link').length, 'Could not find visual tour link.');
  });
});
