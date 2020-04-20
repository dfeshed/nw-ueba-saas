import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | widget/entity-stats', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders stats', async function(assert) {
    await render(hbs`{{widget/entity-stats}}`);
    assert.equal(this.element.textContent.trim(), '');
  });
});
