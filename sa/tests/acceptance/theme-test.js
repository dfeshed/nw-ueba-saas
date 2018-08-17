import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest, login } from '../helpers/setup-login';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { waitForRedux } from '../helpers/wait-for-redux';
import { visit, currentURL, settled } from '@ember/test-helpers';

module('Acceptance | theme', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  hooks.beforeEach(function() {
    localStorage.setItem('reduxPersist:global', JSON.stringify({
      preferences: {
        theme: 'LIGHT'
      }
    }));
    return waitForRedux('global.preferences.theme', 'LIGHT');
  });

  test('theme will rehydrate from local storage on boot', async function(assert) {
    assert.expect(11);

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
    assert.ok(document.cookie.indexOf('nw-ui-theme=light') > -1, 'The theme (light) appears in the nw-ui-theme cookie');

    await login();
    await settled();

    await waitFor(() => document.querySelector('body').classList.contains('dark-theme'));

    const darkTheme = redux.getState().global.preferences.theme;
    assert.equal(darkTheme, 'DARK');
    assert.ok(document.querySelector('body').classList.contains('dark-theme'));
    assert.notOk(document.querySelector('body').classList.contains('light-theme'));
    assert.ok(document.cookie.indexOf('nw-ui-theme=dark') > -1, 'The theme (dark) appears in the nw-ui-theme cookie');

    await settled().then(() => done());
  });
});
