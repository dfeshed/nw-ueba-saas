import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll } from '@ember/test-helpers';

module('Integration | Component | color swatch', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +
    await render(hbs`{{color-swatch}}`);
    assert.ok(findAll('.color-swatch').length, 'Could not find component\'s root DOM element.');
  });
});
