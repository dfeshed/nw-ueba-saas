import rsvp from 'rsvp';
import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import { selectorToExist } from 'ember-wait-for-test-helper/wait-for';
import $ from 'jquery';
import sinon from 'sinon';
import Service from '@ember/service';
import { lookup } from 'ember-dependency-lookup';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const AccessControlService = Service.extend({
  hasInvestigateEmberAccess: true,
  hasInvestigateAccess: true,
  hasInvestigateEventsAccess: true,
  hasInvestigateHostsAccess: true
});

moduleForAcceptance('Acceptance | basic', {
  beforeEach() {
    this.application.register('service:accessControl', AccessControlService);
    this.application.inject('route:application', 'accessControl', 'service:accessControl');
    initialize(this.application);
  }
});

test('visiting /investigate-files', function(assert) {
  visit('/investigate-files');
  andThen(() => {
    assert.equal(currentURL(), '/investigate-files');
  });

});

test('visiting /investigate-files shows server down message', function(assert) {
  const request = lookup('service:request');
  sinon.stub(request, 'ping', () => {
    return new rsvp.Promise((resolve, reject) => reject());
  });

  visit('/investigate-files');

  andThen(() => {
    assert.equal(currentURL(), '/investigate-files');
  });

  waitFor(selectorToExist('.error-page'));

  andThen(() => {
    assert.equal($('.error-page .title').text().trim(), 'Endpoint Server is offline');
  });
});
