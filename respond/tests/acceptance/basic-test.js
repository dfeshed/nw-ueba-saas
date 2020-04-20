
import { module, test } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { visit, currentURL } from '@ember/test-helpers';

module('Acceptance | basic', function(hooks) {
  setupApplicationTest(hooks);

  test('visiting an unknown route redirects to /respond/incidents', async function(assert) {
    assert.expect(1);
    await visit('/respond/blahblah');
    assert.equal(currentURL(), '/respond/incidents', 'The base /respond endpoint should redirect to respond/incidents');

  });

  test('visiting /respond redirects to /respond/incidents', async function(assert) {
    assert.expect(1);
    await visit('/respond');
    assert.equal(currentURL(), '/respond/incidents', 'The base /respond endpoint should redirect to respond/incidents');
  });

  test('visiting /respond/incident/INC-123', async function(assert) {
    assert.expect(1);
    await visit('/respond/incident/INC-123');
    assert.equal(currentURL(), '/respond/incident/INC-123', 'The route loads and we are not redirected');

  });

  test('visiting /respond/alerts', async function(assert) {
    assert.expect(1);

    await visit('/respond/alerts');
    assert.equal(currentURL(), '/respond/alerts', 'The route loads and we are not redirected');
  });

  test('visiting /respond/alert/12345', async function(assert) {
    assert.expect(1);
    await visit('/respond/alert/12345');
    assert.equal(currentURL(), '/respond/alert/12345', 'The route loads and we are not redirected');
  });

  test('visiting /respond/tasks', async function(assert) {
    assert.expect(1);
    await visit('/respond/tasks');
    assert.equal(currentURL(), '/respond/tasks', 'The route loads and we are not redirected');
  });
});
