import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | layout/two-column-layout', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the tow column layout', async function(assert) {
    await render(hbs`{{layout/two-column-layout}}`);
    assert.equal(document.querySelectorAll('.two-column-layout').length, 1);

  });
});
