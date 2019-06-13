import RSVP from 'rsvp';
import { module, test, skip } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import sinon from 'sinon';
import { click, waitFor, visit, currentURL, find } from '@ember/test-helpers';
import { lookup } from 'ember-dependency-lookup';

module('Acceptance | basic', function(hooks) {
  setupApplicationTest(hooks);

  test('visiting /investigate-hosts', async function(assert) {
    await visit('/investigate-hosts');

    assert.equal(currentURL(), '/investigate-hosts');

    /* Test delete flow */
    await waitFor('.rsa-data-table-body-row .rsa-form-checkbox', { timeout: 10000 });

    await click('.rsa-data-table-body-row .rsa-form-checkbox');

  });

  skip('visiting /investigate-hosts shows server down message', async function(assert) {
    const request = lookup('service:request');
    sinon.stub(request, 'ping').callsFake(() => {
      return new RSVP.Promise((resolve, reject) => reject());
    });

    await visit('/investigate-hosts');
    assert.equal(currentURL(), '/investigate-hosts');

    await waitFor('.error-page', { timeout: 10000 });
    assert.equal(find('.error-page .title').text().trim(), 'Endpoint Server is offline');
  });
});
