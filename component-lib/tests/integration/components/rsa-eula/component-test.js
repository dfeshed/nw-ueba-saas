import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { Promise } from 'rsvp';
import { click, find, findAll, render, waitUntil } from '@ember/test-helpers';
import { waitForRaf } from '../../../helpers/wait-for-raf';
import { securitybanner } from './data';
import { patchFetch } from '../../../helpers/patch-fetch';
import sinon from 'sinon';
import { windowProxy } from 'component-lib/utils/window-proxy';

module('Integration | Component | rsa-eula', function(hooks) {
  setupRenderingTest(hooks);

  test('eula can be displayed', async function(assert) {
    assert.expect(1);
    await render(hbs`{{rsa-eula displayEula=true}}`);

    assert.equal(findAll('.eula-content').length, 1);
  });

  test('eula can be bypassed', async function(assert) {
    assert.expect(1);
    await render(hbs`{{rsa-eula displayEula=false}}`);
    assert.equal(findAll('.eula-content').length, 0);
  });

  test('eula button disabled when eulaContent not available (enabled after xhr done)', async function(assert) {
    assert.expect(3);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
            return '<h1>eula</h1>';
          }
        });
      });
    });

    const selector = '[test-id=btnAcceptEula] button';
    await render(hbs`{{rsa-eula displayEula=true}}`);

    assert.equal(findAll('.eula-content').length, 1);
    assert.equal(find(selector).disabled, true);

    await waitForRaf();
    await waitUntil(() => find(selector).disabled !== true, { timeout: 2000 });
    assert.equal(find(selector).disabled, false);
  });

  test('eula button disabled when eulaContent not available (even after xhr fail)', async function(assert) {
    assert.expect(3);

    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject('boom!');
      });
    });

    await render(hbs`{{rsa-eula displayEula=true}}`);

    assert.equal(findAll('.eula-content').length, 1);
    assert.equal(find('[test-id=btnAcceptEula] button').disabled, true);

    await waitForRaf();
    assert.equal(find('[test-id=btnAcceptEula] button').disabled, true);
  });

  test('eula html will be sanitized before html is rendered', async function(assert) {
    assert.expect(2);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
            return '<p><img src="#" onerror=alert(1) />foo</p>';
          }
        });
      });
    });

    await render(hbs`{{rsa-eula displayEula=true}}`);
    assert.equal(findAll('.eula-content').length, 1);
    assert.equal(find('.eula-content').innerHTML.trim(), '<p><img src="#">foo</p>');
  });

  test('security header title and text will be sanitized before html is rendered', async function(assert) {
    assert.expect(4);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return securitybanner;
          }
        });
      });
    });

    await render(hbs`{{rsa-eula displayEula=false displaySecurityBanner=true}}`);

    assert.equal(findAll('[test-id=securityBannerTitle]').length, 1);
    assert.equal(find('[test-id=securityBannerTitle]').innerHTML.trim(), 'Terms and Conditions <img src="a">');

    assert.equal(findAll('[test-id=securityBannerText]').length, 1);
    assert.equal(find('[test-id=securityBannerText]').innerHTML.trim(), 'banner text example <img src="a">');
  });

  test('security header title will render login eula title when displayEula truthy and displaySecurityBanner falsy', async function(assert) {
    assert.expect(2);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
            return '<p><img src="#" onerror=alert(1) />foo</p>';
          }
        });
      });
    });

    await render(hbs`{{rsa-eula displayEula=true}}`);

    assert.equal(findAll('[test-id=securityBannerTitle]').length, 1);
    assert.equal(find('[test-id=securityBannerTitle]').innerHTML.trim(), 'End User License Agreement');
  });

  test('Security banner not displayed', async function(assert) {
    assert.expect(1);

    await render(hbs`{{rsa-eula displayEula=false displaySecurityBanner=false}}`);
    assert.equal(findAll('.banner-content').length, 0);
  });

  test('Security banner displayed', async function(assert) {
    assert.expect(1);

    await render(hbs`{{rsa-eula displayEula=false displaySecurityBanner=true}}`);
    assert.equal(findAll('.banner-content').length, 1);
  });

  test('Security banner can be dismissed', async function(assert) {
    assert.expect(3);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return securitybanner;
          }
        });
      });
    });

    await render(hbs`{{rsa-eula displayEula=false displaySecurityBanner=true}}`);

    const buttons = findAll('.rsa-form-button');
    assert.equal(buttons.length, 1, 'One button exists');
    assert.equal(buttons[0].textContent.trim(), 'Agree', 'Button text is Agree');

    await click(buttons[0]);
    assert.equal(findAll('.banner-content').length, 0, 'Clicking the Accept button dismisses the login banner');
  });

  test('should redirect to saml login url if single sign on is enabled', async function(assert) {
    assert.expect(2);

    let currentUrl = null;
    let newTab = true;

    const locationStub = sinon.stub(windowProxy, 'openInCurrentTab').callsFake((urlPassed) => {
      currentUrl = urlPassed;
      newTab = false;
    });

    await render(hbs`{{rsa-eula displayEula=false displaySecurityBanner=false isSsoEnabled=true}}`);

    assert.equal(currentUrl, '/saml/login', 'redirect to saml login url if sso is enabled');
    assert.notOk(newTab);
    locationStub.restore();
  });

  test('should redirect to normal login url if single sign on is disabled', async function(assert) {
    assert.expect(1);

    await render(hbs`{{rsa-eula displayEula=false displaySecurityBanner=false isSsoEnabled=false}}`);

    assert.notEqual(findAll('.login').length, 0);
  });

});
