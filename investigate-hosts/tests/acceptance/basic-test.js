import RSVP from 'rsvp';
import { module, test, skip } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import $ from 'jquery';
import sinon from 'sinon';
import { click, waitFor, visit, currentURL } from '@ember/test-helpers';
import { lookup } from 'ember-dependency-lookup';

const deleteButton = '.host-table__toolbar-buttons .delete-host-button button';
const popUpCont = '.rsa-application-modal-content';

module('Acceptance | basic', function(hooks) {
  setupApplicationTest(hooks);

  test('visiting /investigate-hosts', async function(assert) {
    await visit('/investigate-hosts');

    assert.equal(currentURL(), '/investigate-hosts');

    /* Test delete flow */
    await waitFor('.rsa-data-table-body-row .rsa-form-checkbox', { timeout: 10000 });

    assert.equal($(deleteButton)[0].innerText, 'Delete');
    assert.equal($(deleteButton).attr('disabled'), 'disabled');
    await click('.rsa-data-table-body-row .rsa-form-checkbox');

    assert.equal($(deleteButton).attr('disabled'), undefined);
    assert.equal($(popUpCont).length, 0);
    await click(deleteButton);

    assert.equal($(popUpCont).length, 1);
    assert.equal($(popUpCont).find('h3')[0].innerText, 'Delete 1 host(s)');
    /* End - Test delete flow */
  });

  skip('visiting /investigate-hosts shows server down message', async function(assert) {
    const request = lookup('service:request');
    sinon.stub(request, 'ping').callsFake(() => {
      return new RSVP.Promise((resolve, reject) => reject());
    });

    await visit('/investigate-hosts');
    assert.equal(currentURL(), '/investigate-hosts');

    await waitFor('.error-page', { timeout: 10000 });
    assert.equal($('.error-page .title').text().trim(), 'Endpoint Server is offline');
  });
});
