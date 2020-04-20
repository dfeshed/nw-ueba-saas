import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-page-layout/zone', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    await render(hbs`{{rsa-page-layout/zone}}`);

    assert.equal(this.element.textContent.trim(), '');

    // Template block usage:
    await render(hbs`
      {{#rsa-page-layout/zone zone='left-zone'}}
        template block text
      {{/rsa-page-layout/zone}}
    `);
    assert.equal(findAll('.left-zone').length, 1, 'class is properly set');
    assert.equal(find('.left-zone').textContent.trim(), 'template block text');
  });
});
