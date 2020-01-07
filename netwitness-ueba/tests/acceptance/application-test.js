import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest, login } from '../helpers/setup-login';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { visit, currentURL, settled, waitUntil } from '@ember/test-helpers';

module('Acceptance | application', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  test('Investigate User is visible after login', async function(assert) {
    assert.expect(2);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    await settled();

    await login();

    await waitUntil(() => currentURL() === '/investigate/entities', { timeout: 6000 });
    assert.equal(currentURL(), '/investigate/entities');

    await settled().then(() => done());
  });

});
