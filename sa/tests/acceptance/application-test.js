import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest, login } from '../helpers/setup-login';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { findAll, visit, currentURL, settled, waitUntil } from '@ember/test-helpers';

module('Acceptance | application', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  test('Configure tab is visible when isNwUIPrimary is set', async function(assert) {
    assert.expect(3);
    this.owner.lookup('service:session').set('isNwUIPrimary', true);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    await settled();

    await login();

    await waitUntil(() => currentURL() === '/respond/incidents', { timeout: 6000 });
    assert.equal(currentURL(), '/respond/incidents');

    assert.equal(findAll('.rsa-application-header .rsa-header-nav-configure').length, 1, 'configure primary tab is visible when isNwUIPrimary is set to true');

    await settled().then(() => done());
  });

  test('Configure tab is hidden when isNwUIPrimary is set to false', async function(assert) {
    assert.expect(3);
    this.owner.lookup('service:session').set('isNwUIPrimary', false);

    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    await settled();

    await login();

    await waitUntil(() => currentURL() === '/respond/incidents', { timeout: 6000 });
    assert.equal(currentURL(), '/respond/incidents');

    assert.equal(findAll('.rsa-application-header .rsa-header-nav-configure').length, 0, 'configure primary tab is hidden when isNwUIPrimary is set to false');

    await settled().then(() => done());
  });

  test('Admin tab redirects to classic monitoring page when hasAdminAccess is true and isNwUIPrimary is false', async function(assert) {
    assert.expect(3);
    this.owner.lookup('service:accessControl').set('hasAdminAccess', true);
    this.owner.lookup('service:session').set('isNwUIPrimary', false);
    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    await settled();

    await login();

    await waitUntil(() => currentURL() === '/respond/incidents', { timeout: 6000 });
    assert.equal(currentURL(), '/respond/incidents');

    assert.equal(findAll('.rsa-application-header .rsa-header-nav-admin')[0].pathname, '/admin/monitoring', 'Admin tab redirects to monitoring page when hasAdminAccess is true and isNwUIPrimary is set to false');

    await settled().then(() => done());
  });

  test('Admin tab does not redirect to classic monitoring page when hasAdminAccess is true and isNwUIPrimary is true', async function(assert) {
    assert.expect(3);
    this.owner.lookup('service:accessControl').set('hasAdminAccess', true);
    this.owner.lookup('service:session').set('isNwUIPrimary', true);
    const done = waitForSockets();

    await visit('/');

    assert.equal(currentURL(), '/login');

    await settled();

    await login();

    await waitUntil(() => currentURL() === '/respond/incidents', { timeout: 6000 });
    assert.equal(currentURL(), '/respond/incidents');

    assert.notEqual(findAll('.rsa-application-header .rsa-header-nav-admin')[0].pathname, '/admin/monitoring', 'Admin tab does not redirect to monitoring page when both hasAdminAccess and isNwUIPrimary is true');

    await settled().then(() => done());
  });
});
