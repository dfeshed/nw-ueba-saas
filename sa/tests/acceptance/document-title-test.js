import { module, test } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest } from '../helpers/setup-login';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { visit, currentURL, settled } from '@ember/test-helpers';

module('Acceptance | document title', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  test('title will be set correctly on application boot', async function(assert) {
    assert.expect(2);
    const done = waitForSockets();
    await visit('/');
    assert.equal(currentURL(), '/login', 'currentUrl is /login');
    await settled();
    const translation = this.owner.lookup('service:i18n');
    const expected = translation.t('appTitle');
    // multiple <title> in document
    // like title "SA Tests" for the test
    // not just "NetWitness Platform" (expected)
    const titles = document.querySelectorAll('title');
    const found = Array.from(titles).find((title) => title.textContent.trim() === expected);
    assert.ok(found, 'document title found');
    await settled().then(() => done());
  });
});
