import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | widget/widget-title', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders titles', async function(assert) {

    await render(hbs`
      {{#widget/widget-title}}
        Test
      {{/widget/widget-title}}
    `);

    assert.equal(this.element.textContent.trim(), 'Test');
  });
});
