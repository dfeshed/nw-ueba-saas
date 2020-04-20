import { module, test } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { visit, currentURL, waitFor, find } from '@ember/test-helpers';
import { waitForSockets } from '../helpers/wait-for-sockets';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';
import Request from 'streaming-data/services/data-access/requests';

module('Acceptance | basic', function(hooks) {
  setupApplicationTest(hooks);
  test('visiting /investigate-files', async function(assert) {
    await visit('/investigate-files');
    assert.equal(currentURL(), '/investigate-files');
  });

  test('visiting /investigate-files/certificates', async function(assert) {
    assert.expect(1);
    const done = waitForSockets();
    await visit('/investigate-files/certificates');
    assert.equal(currentURL(), '/investigate-files/certificates', 'The route loads and we are not redirected');
    return wait().then(() => done());
  });

  test('visiting /investigate-files/1234', async function(assert) {
    assert.expect(1);

    const done = waitForSockets();

    await visit('/investigate-files/1234');
    assert.equal(currentURL(), '/investigate-files/1234', 'The route loads and we are not redirected');
    return wait().then(() => done());

  });

  test('visiting /investigate-files shows server down message', async function(assert) {
    sinon.stub(Request, 'ping').rejects();

    await visit('/investigate-files');

    assert.equal(currentURL(), '/investigate-files');

    await waitFor('.error-page', { timeout: 10000 });
    assert.equal(find('.error-page .title').textContent.trim(), 'Endpoint Server is offline');
  });
});


