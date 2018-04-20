import { Promise } from 'rsvp';
import { get } from '@ember/object';
import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest, login } from '../helpers/setup-login';
import { waitForRedux } from '../helpers/wait-for-redux';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { patchFetch } from 'sa/tests/helpers/patch-fetch';
import { visit, currentURL, settled } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';

const english = { id: 'en_US', key: 'en-us', label: 'english' };
const spanish = { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' };
const german = { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' };
const japanese = { id: 'ja_JP', key: 'ja-jp', label: 'japanese', fileName: 'japanese_ja-jp.js' };

const setupLocalStorage = (locale, locales) => {
  localStorage.setItem('reduxPersist:global', JSON.stringify({
    preferences: {
      locale,
      locales
    }
  }));
};

module('Acceptance | locales', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  test('locales are fetched and persisted before login', async function(assert) {
    assert.expect(6);

    setupLocalStorage(english, [english]);
    await waitForRedux('global.preferences.locale.id', english.id);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    const i18n = this.owner.lookup('service:i18n');
    assert.equal(get(i18n, 'locale'), 'en-us');

    const redux = this.owner.lookup('service:redux');
    const { locale, locales } = redux.getState().global.preferences;
    assert.deepEqual(locale, english);
    assert.deepEqual(locales, [english, german, japanese, spanish]);

    await login();

    assert.equal(currentURL(), '/respond/incidents');
    assert.equal(get(i18n, 'locale'), 'en-us');

    return settled().then(() => done());
  });

  test('users locale preference will update i18n locale after successful login', async function(assert) {
    assert.expect(6);

    setupLocalStorage(spanish, [english, spanish]);
    await waitForRedux('global.preferences.locale.id', spanish.id);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    const i18n = this.owner.lookup('service:i18n');
    assert.equal(get(i18n, 'locale'), 'es-mx');

    const redux = this.owner.lookup('service:redux');
    const { locale, locales } = redux.getState().global.preferences;
    assert.deepEqual(locale, spanish);
    assert.deepEqual(locales, [english, german, japanese, spanish]);

    await login();

    assert.equal(currentURL(), '/respond/incidents');
    assert.equal(get(i18n, 'locale'), 'en-us');

    return settled().then(() => done());
  });

  test('application title will update when i18n locale changed', async function(assert) {
    assert.expect(5);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    const headData = this.owner.lookup('service:headData');
    assert.equal(get(headData, 'title').toString(), 'NetWitness Platform');

    await login();

    assert.equal(currentURL(), '/respond/incidents');
    assert.equal(get(headData, 'title').toString(), 'Incidents - Respond - NetWitness Platform');

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
            return new Promise(function(r) {
              r("define('sa/locales/ja-jp/translations', ['exports'], function (exports) { exports.default = { respond: { title: 'JA_Respond' } }; })");
            });
          }
        });
      });
    });

    const powerSelect = '[test-id=locale-preferences] .power-select';
    clickTrigger(powerSelect);
    selectChoose(`${powerSelect} .ember-power-select-trigger`, 'Japanese');

    assert.equal(get(headData, 'title').toString(), 'Incidents - JA_Respond - NetWitness Platform');

    return settled().then(() => done());
  });
});
