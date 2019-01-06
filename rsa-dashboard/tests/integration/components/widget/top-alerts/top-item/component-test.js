import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | widget/top-alerts/top-item', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the top risk information', async function(assert) {
    await render(hbs`{{widget/top-alerts/top-item}}`);
    assert.equal(document.querySelectorAll('[test-id=entity-info-title]').length, 1, 'Title rendered');
    assert.equal(document.querySelectorAll('[test-id=additional-info]').length, 1, 'Additional info section exists');
    assert.equal(document.querySelectorAll('[test-id=incident-info]').length, 1, 'Incident section exists');
  });
});
