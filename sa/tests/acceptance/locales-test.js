import { get } from '@ember/object';
import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest, login } from '../helpers/setup-login';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { visit, currentURL, settled } from '@ember/test-helpers';

const english = { id: 'en-us', label: 'english' };
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
    localStorage.removeItem('reduxPersist:global');
  });

  test('locales are fetched and persisted before login', async function(assert) {
    assert.expect(6);

    const english = { id: 'en-us', label: 'english' };
    setupLocalStorage(english, [english]);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    const i18n = this.owner.lookup('service:i18n');
    assert.equal(get(i18n, 'locale'), 'en-us');

    const redux = this.owner.lookup('service:redux');
    const { locale, locales } = redux.getState().global.preferences;
    assert.deepEqual(locale, english);
    assert.deepEqual(locales, [english, german, spanish]);

    await login();

    assert.equal(currentURL(), '/respond/incidents');
    assert.equal(get(i18n, 'locale'), 'en-us');

    return settled().then(() => done());
  });

  test('users locale preference will update i18n locale after successful login', async function(assert) {
    assert.expect(6);

    setupLocalStorage(spanish, [english, spanish]);

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
    assert.equal(get(i18n, 'locale'), 'en-us');

    return settled().then(() => done());
  });
});
