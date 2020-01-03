import { module, skip } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest } from '../helpers/setup-login';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { waitUntil, visit, currentURL, settled } from '@ember/test-helpers';

const timeout = 20000;

module('Acceptance | document title', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  skip('title will be set correctly on application boot', async function(assert) {
    assert.expect(2);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    const translation = this.owner.lookup('service:i18n');
    const expected = translation.t('appTitle').string;

    await waitUntil(() => {
      return document.title === expected;
    }, { timeout });
    assert.equal(document.title, expected);

    await settled().then(() => done());
  });
});
