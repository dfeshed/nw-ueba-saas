import { module, test } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import sinon from 'sinon';
import { click, waitFor, visit, currentURL, find } from '@ember/test-helpers';
import Request from 'streaming-data/services/data-access/requests';

module('Acceptance | basic', function(hooks) {
  setupApplicationTest(hooks);

  test('visiting /investigate-hosts', async function(assert) {
    await visit('/investigate-hosts');

    assert.equal(currentURL(), '/investigate-hosts');

    /* Test delete flow */
    await waitFor('.rsa-data-table-body-row .rsa-form-checkbox', { timeout: 10000 });

    await click('.rsa-data-table-body-row .rsa-form-checkbox');

  });

  test('visiting /investigate-hosts shows server down message', async function(assert) {
    sinon.stub(Request, 'ping').rejects();

    await visit('/investigate-hosts');
    assert.equal(currentURL(), '/investigate-hosts');

    await waitFor('.error-page', { timeout: 10000 });
    assert.equal(find('.error-page .title').textContent.trim(), 'Endpoint Server is offline');
  });

});
