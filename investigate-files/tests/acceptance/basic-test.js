import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import { selectorToExist } from 'ember-wait-for-test-helper/wait-for';
import $ from 'jquery';
import requests from 'streaming-data/services/data-access/requests';
import sinon from 'sinon';

const {
  RSVP
} = Ember;


moduleForAcceptance('Acceptance | basic', {
});

test('visiting /investigate-files', function(assert) {
  visit('/investigate-files');
  andThen(() => {
    assert.equal(currentURL(), '/investigate-files');
  });
  /* Test delete flow */
  waitFor(selectorToExist('.rsa-investigate-files'));
  andThen(() => {
    assert.equal($('.filter-list__item').length, 3, 'Displaying all default filters');
  });

});

test('visiting /investigate-files shows server down message', function(assert) {

  sinon.stub(requests, 'ping').returns(RSVP.reject('Error'));

  visit('/investigate-files');

  andThen(() => {
    assert.equal(currentURL(), '/investigate-files');
  });

  waitFor(selectorToExist('.error-page'));

  andThen(() => {
    assert.equal($('.error-page .title').text().trim(), 'Endpoint Server is offline');
  });
});
