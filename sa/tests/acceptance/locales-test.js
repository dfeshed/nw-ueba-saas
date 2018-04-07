import { get } from '@ember/object';
import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { localStorageClear } from '../helpers/wait-for';
import { setupLoginTest, login } from '../helpers/setup-login';
import { waitForRedux } from '../helpers/wait-for-redux';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { visit, currentURL, settled } from '@ember/test-helpers';

const english = { id: 'en_US', label: 'english' };
const spanish = { id: 'es', label: 'spanish', fileName: 'spanish_es.js' };
const german = { id: 'de-DE', label: 'german', fileName: 'german_de-DE.js' };

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

  hooks.afterEach(function() {
    return localStorageClear();
  });

  test('locales are fetched and persisted before login', async function(assert) {
    assert.expect(6);

    setupLocalStorage(english, [english]);
    await waitForRedux(this, 'global.preferences.locale.id', english.id);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    const i18n = this.owner.lookup('service:i18n');
    assert.equal(get(i18n, 'locale'), 'en_US');

    const redux = this.owner.lookup('service:redux');
    const { locale, locales } = redux.getState().global.preferences;
    assert.deepEqual(locale, english);
    assert.deepEqual(locales, [english, german, spanish]);

    await login();

    assert.equal(currentURL(), '/respond/incidents');
    assert.equal(get(i18n, 'locale'), 'en_US');

    return settled().then(() => done());
  });

  test('users locale preference will update i18n locale after successful login', async function(assert) {
    assert.expect(6);

    setupLocalStorage(spanish, [english, spanish]);
    await waitForRedux(this, 'global.preferences.locale.id', spanish.id);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    const i18n = this.owner.lookup('service:i18n');
    assert.equal(get(i18n, 'locale'), 'es');

    const redux = this.owner.lookup('service:redux');
    const { locale, locales } = redux.getState().global.preferences;
    assert.deepEqual(locale, spanish);
    assert.deepEqual(locales, [english, german, spanish]);

    await login();

    assert.equal(currentURL(), '/respond/incidents');
    assert.equal(get(i18n, 'locale'), 'en_US');

    return settled().then(() => done());
  });
});
