import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest, login } from '../helpers/setup-login';
import { localStorageClear } from '../helpers/wait-for';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { visit, currentURL, settled } from '@ember/test-helpers';

module('Acceptance | theme', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  hooks.beforeEach(function() {
    return new Promise((resolve) => {
      localStorage.setItem('reduxPersist:global', JSON.stringify({
        preferences: {
          theme: 'LIGHT'
        }
      }));
      next(resolve);
    });
  });

  hooks.afterEach(function() {
    return localStorageClear();
  });

  test('theme will rehydrate from local storage on boot', async function(assert) {
    assert.expect(9);

    const done = waitForSockets();

    assert.ok(document.querySelector('body').classList.contains('dark-theme'));
    assert.notOk(document.querySelector('body').classList.contains('light-theme'));

    await visit('/');

    assert.equal(currentURL(), '/login');

    const redux = this.owner.lookup('service:redux');
    const lightTheme = redux.getState().global.preferences.theme;
    assert.equal(lightTheme, 'LIGHT');
    assert.ok(document.querySelector('body').classList.contains('light-theme'));
    assert.notOk(document.querySelector('body').classList.contains('dark-theme'));

    await login();

    await settled();
    await waitFor(() => document.querySelector('body').classList.contains('dark-theme'));

    const darkTheme = redux.getState().global.preferences.theme;
    assert.equal(darkTheme, 'DARK');
    assert.ok(document.querySelector('body').classList.contains('dark-theme'));
    assert.notOk(document.querySelector('body').classList.contains('light-theme'));

    done();
  });
});
