import { find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-form-checkbox', function(hooks) {
  setupRenderingTest(hooks);

  test('has the base class', async function(assert) {
    await render(hbs `{{rsa-form-checkbox}}`);
    assert.ok(find('input').classList.contains('rsa-form-checkbox'));
  });

  test('has the checked class', async function(assert) {
    await render(hbs `{{rsa-form-checkbox checked=true}}`);
    assert.ok(find('input').classList.contains('checked'));
  });

  test('has the disabled class', async function(assert) {
    await render(hbs `{{rsa-form-checkbox disabled=true}}`);
    assert.ok(find('input').classList.contains('disabled'));
  });
});
