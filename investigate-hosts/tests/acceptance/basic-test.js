import RSVP from 'rsvp';
import { test, skip } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { selectorToExist } from 'ember-wait-for-test-helper/wait-for';
import $ from 'jquery';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { lookup } from 'ember-dependency-lookup';
const deleteButton = '.host-table__toolbar-buttons .delete-host-button button';
const popUpCont = '.rsa-application-modal-content';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('endpoint'),
  beforeEach() {
    initialize(this.application);
  }
});

test('visiting /investigate-hosts', function(assert) {
  visit('/investigate-hosts');
  andThen(() => {
    assert.equal(currentURL(), '/investigate-hosts');
  });
  /* Test delete flow */
  waitFor(selectorToExist('.rsa-data-table-body-row'));
  andThen(() => {
    assert.equal($(deleteButton)[0].innerText, 'Delete');
    assert.equal($(deleteButton).attr('disabled'), 'disabled');
    click('.rsa-data-table-body-row:first .rsa-form-checkbox');
  });
  andThen(() => {
    assert.equal($(deleteButton).attr('disabled'), undefined);
  });
  andThen(() => {
    assert.equal($(popUpCont).length, 0);
  });
  andThen(() => {
    click(deleteButton);
  });
  andThen(() => {
    assert.equal($(popUpCont).length, 1);
    assert.equal($(popUpCont).find('h3')[0].innerText, 'Delete 1 host(s)');
  });
  /* End - Test delete flow */
});

skip('visiting /investigate-hosts shows server down message', function(assert) {

  const request = lookup('service:request');
  sinon.stub(request, 'ping', () => {
    return new RSVP.Promise((resolve, reject) => reject());
  });

  visit('/investigate-hosts');

  andThen(() => {
    assert.equal(currentURL(), '/investigate-hosts');
  });

  waitFor(selectorToExist('.error-page'));

  andThen(() => {
    assert.equal($('.error-page .title').text().trim(), 'Endpoint Server is offline');
  });
});
