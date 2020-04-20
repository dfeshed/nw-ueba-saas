import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | layout/layout-column', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the items', async function(assert) {
    this.set('items', ['Test - 1', 'Test - 2']);
    // Template block usage:
    await render(hbs`
      {{#layout/layout-column items=items}}
        <div class="test">{{items}}</div>
      {{/layout/layout-column}}
    `);

    assert.equal(document.querySelectorAll('.test').length, 1, 'one items rendered');
  });
});
