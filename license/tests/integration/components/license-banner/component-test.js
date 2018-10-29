import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render, waitUntil, click, findAll } from '@ember/test-helpers';
import RSVP from 'rsvp';
import sinon from 'sinon';
import { throwSocket } from '../../../helpers/patch-socket';
import moment from 'moment';

const mockService = function(owner, compliant, status) {
  const licenseService = owner.lookup('service:license');
  return sinon.stub(licenseService, 'getCompliance', function() {
    return new RSVP.Promise((resolve) => {
      // mimic 1 sec delayed response
      setTimeout(() => {
        resolve({ compliant, compliances: [{ status }] });
      }, 1000);
    });
  });
};

const getExpectedMsg = function(owner, i18nKey) {
  const i18n = owner.lookup('service:i18n');
  if (i18nKey === 'license.banner.serverDown') {
    return i18n.t(i18nKey).toString() + i18n.t('license.banner.servicesPage').toString();
  }
  return i18n.t(i18nKey).toString() + i18n.t('license.banner.licensePage').toString();
};

module('license-banner', 'Integration | Component | License Banner', function(hooks) {

  setupRenderingTest(hooks);

  hooks.afterEach(function() {
    sessionStorage.removeItem('rsa-license-banner-dismissed');
  });

  test('License banner renders in error mode for EXPIRED compliance ', async function(assert) {
    await render(hbs `{{license-banner}}`);
    assert.equal(this.$('.license-banner').length, 1, 'License banner rendered.');
    await waitUntil(() => find('.license-banner.shown'), { timeout: 3000 });
    assert.ok(!find('.dismiss-btn .rsa-form-button'), 'Error banner does not have dismiss button');
    assert.equal(find('.banner-msg').textContent.trim(), getExpectedMsg(this.owner, 'license.banner.expired'), 'Correct banner message should be shown');
  });

  test('License banner renders in warning mode for NEARING_EXPIRY compliance', async function(assert) {
    const stub = mockService(this.owner, false, 'NEARING_EXPIRY');
    await render(hbs `{{license-banner}}`);
    assert.equal(this.$('.license-banner').length, 1, 'Warning banner rendered.');
    await waitUntil(() => find('.license-banner.shown'), { timeout: 3000 });
    assert.equal(find('.banner-msg').textContent.trim(), getExpectedMsg(this.owner, 'license.banner.near-expiry'), 'Correct banner message should be shown');
    assert.equal(findAll('.dismiss-btn .rsa-form-button').length, 1, 'Warning banner must have dismiss button');
    stub.restore();
  });

  test('License banner renders in warning mode for USAGE_LIMIT_NEARING compliance', async function(assert) {
    const stub = mockService(this.owner, false, 'USAGE_LIMIT_NEARING');
    await render(hbs `{{license-banner}}`);
    assert.equal(this.$('.license-banner').length, 1, 'Warning banner rendered.');
    await waitUntil(() => find('.license-banner.shown'), { timeout: 3000 });
    assert.equal(find('.banner-msg').textContent.trim(), getExpectedMsg(this.owner, 'license.banner.near-usage-limit'), 'Correct banner message should be shown');
    assert.equal(findAll('.dismiss-btn .rsa-form-button').length, 1, 'Warning banner must have dismiss button');
    stub.restore();
  });

  test('License banner in warning mode can be dismissed and it will not reappear', async function(assert) {
    const stub = mockService(this.owner, false, 'USAGE_LIMIT_NEARING');
    await render(hbs `{{license-banner}}`);
    await waitUntil(() => find('.license-banner.shown'), { timeout: 3000 });
    const dismissBtn = find('.dismiss-btn .rsa-form-button');
    await click(dismissBtn);
    assert.ok(!find('.banner-content.shown'), 'Clicking the Dismiss button dismisses the banner');
    assert.equal(sessionStorage.getItem('rsa-license-banner-dismissed'), 'true', 'Banner dismissed flag must be set in session storage');
    const done = assert.async();

    await render(hbs `{{license-banner}}`);

    // Wait for 3 seconds and see if banner shows up
    setTimeout(function() {
      assert.ok(!find('.banner-content.shown'), 'Warning banner should not be shown after dismissing');
      done();
      stub.restore();
    }, 3000);
  });

  test('License banner renders in error mode for when License Server is down', async function(assert) {
    const stub = mockService(this.owner, false, 'LICENSE_SERVER_DOWN');
    await render(hbs `{{license-banner}}`);
    assert.equal(this.$('.license-banner').length, 1, 'License banner rendered.');
    await waitUntil(() => find('.license-banner.shown'), { timeout: 3000 });
    assert.ok(!find('.dismiss-btn .rsa-form-button'), 'Error banner does not have dismiss button');
    assert.equal(find('.banner-msg').textContent.trim(), getExpectedMsg(this.owner, 'license.banner.serverDown'), 'Correct banner message should be shown');
    stub.restore();
  });

  test('When License Server is down, exception is thrown and banner renders in error mode', async function(assert) {
    // Set License server down date to 5 days prior
    const fiveDaysInMillis = moment.duration(5, 'days').asMilliseconds();
    localStorage.setItem('compliance-last-fetched-date', new Date().getTime() - fiveDaysInMillis);

    // Throw Exception to simulate Licese Server down case
    const done = throwSocket({ methodToThrow: 'get', modelNameToThrow: 'license-compliance', message: 'Connection to LS lost' });

    // Test if License Server down banner is shown
    await render(hbs `{{license-banner}}`);
    assert.equal(this.$('.license-banner').length, 1, 'License banner rendered.');
    await waitUntil(() => find('.license-banner.shown'), { timeout: 3000 });
    assert.ok(!find('.dismiss-btn .rsa-form-button'), 'Error banner does not have dismiss button');
    assert.equal(find('.banner-msg').textContent.trim(), getExpectedMsg(this.owner, 'license.banner.serverDown'), 'Correct banner message should be shown');
    done();
  });
});
