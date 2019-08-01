import { Promise } from 'rsvp';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import { patchFetch } from '../../../helpers/patch-fetch';

module('Integration | Component | rsa-routable-login', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    assert.expect(1);
    await render(hbs`{{rsa-routable-login}}`);
    assert.equal(findAll('.rsa-login').length, 1);
  });

  test('the submit is disabled by default', async function(assert) {
    assert.expect(1);
    await render(hbs`{{rsa-routable-login}}`);
    assert.equal(document.querySelector('button[type=submit]').disabled, true);
  });

  test('the submit is enabled after entering values', async function(assert) {
    assert.expect(1);
    await render(hbs`{{rsa-routable-login username='foo' password='bar'}}`);
    assert.equal(document.querySelector('button[type=submit]').disabled, false);
  });

  test('the has-error class is added to .login-wrapper when hasError is true', async function(assert) {
    assert.expect(1);
    await render(hbs`{{rsa-routable-login errorMessage='foo'}}`);
    assert.equal(document.querySelector('.login-wrapper').classList.contains('has-error'), true);
  });

  test('if pki is enabled, the Input Fields are hidden automatically', async function(assert) {

    assert.expect(2);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return 'on';
          }
        });
      });
    });

    // Render UI
    await render(hbs`{{rsa-routable-login}}`);

    // Assert that username and password are hidden
    assert.equal(document.querySelector('[test-id=loginPassword]').style.display, 'none');
    assert.equal(document.querySelector('[test-id=loginUsername]').style.display, 'none');
  });
});
