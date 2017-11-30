import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from '../helpers/engine-resolver';
import { selectorToExist } from 'ember-wait-for-test-helper/wait-for';
import $ from 'jquery';
import requests from 'streaming-data/services/data-access/requests';
import sinon from 'sinon';

const {
  RSVP
} = Ember;

const deleteButton = '.host-table__toolbar-buttons .rsa-form-button-wrapper:last-child button';
const popUpCont = '.rsa-application-modal-content';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('endpoint')
});

test('visiting /investigate-hosts', function(assert) {
  visit('/investigate-hosts');
  andThen(() => {
    assert.equal(currentURL(), '/investigate-hosts');
  });
  /* Test delete flow */
  waitFor(selectorToExist('.rsa-form-checkbox'));
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

test('visiting /investigate-hosts shows server down message', function(assert) {

  sinon.stub(requests, 'ping').returns(RSVP.reject('Error'));

  visit('/investigate-hosts');

  andThen(() => {
    assert.equal(currentURL(), '/investigate-hosts');
  });

  waitFor(selectorToExist('.error-page'));

  andThen(() => {
    assert.equal($('.error-page .title').text().trim(), 'Endpoint Server is offline');
  });
});
