import { Promise } from 'rsvp';
import { module, test } from 'qunit';
import Service from '@ember/service';
import { later } from '@ember/runloop';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, settled } from '@ember/test-helpers';

module('Integration | Component | rsa-routable-login', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    assert.expect(1);
    await render(hbs `{{rsa-routable-login}}`);
    assert.equal(findAll('.rsa-login').length, 1);
  });

  test('the submit is disabled by default', async function(assert) {
    assert.expect(1);
    await render(hbs `{{rsa-routable-login displayEula=false}}`);
    assert.equal(document.querySelector('button[type=submit]').disabled, true);
  });

  test('the submit is enabled after entering values', async function(assert) {
    assert.expect(1);
    await render(hbs `{{rsa-routable-login displayEula=false username='foo' password='bar'}}`);
    assert.equal(document.querySelector('button[type=submit]').disabled, false);
  });

  test('eula can be displayed', async function(assert) {
    assert.expect(1);
    await render(hbs `{{rsa-routable-login displayEula=true}}`);
    assert.equal(findAll('.eula-content').length, 1);
  });

  test('eula button disabled when eulaContent not available (enabled after xhr done)', async function(assert) {
    assert.expect(3);

    this.owner.register('service:ajax', Service.extend({
      request: () => {
        return new Promise(function(resolve) {
          later(() => {
            resolve('<h1>eula</h1>');
          }, 1);
        });
      }
    }));

    await render(hbs `{{rsa-routable-login displayEula=true}}`);
    assert.equal(findAll('.eula-content').length, 1);
    assert.equal(document.querySelector('[test-id=btnAcceptEula] button').disabled, true);

    await settled();
    assert.equal(document.querySelector('[test-id=btnAcceptEula] button').disabled, false);
  });

  test('eula button disabled when eulaContent not available (even after xhr fail)', async function(assert) {
    assert.expect(3);

    this.owner.register('service:ajax', Service.extend({
      request: () => {
        return new Promise(function(resolve, reject) {
          later(() => {
            reject('boom!');
          }, 1);
        });
      }
    }));

    await render(hbs `{{rsa-routable-login displayEula=true}}`);
    assert.equal(findAll('.eula-content').length, 1);
    assert.equal(document.querySelector('[test-id=btnAcceptEula] button').disabled, true);

    await settled();
    assert.equal(document.querySelector('[test-id=btnAcceptEula] button').disabled, true);
  });

  test('eula can be bypassed', async function(assert) {
    assert.expect(1);
    await render(hbs `{{rsa-routable-login displayEula=false}}`);
    assert.equal(findAll('.eula-content').length, 0);
  });

  test('the has-error class is added to .login-wrapper when hasError is true', async function(assert) {
    assert.expect(1);
    await render(hbs `{{rsa-routable-login hasError=true}}`);
    assert.equal(document.querySelector('.login-wrapper').classList.contains('has-error'), true);
  });

});
