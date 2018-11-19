import rsvp from 'rsvp';
import { test, skip } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import $ from 'jquery';
import sinon from 'sinon';
import Service from '@ember/service';
import { lookup } from 'ember-dependency-lookup';
import wait from 'ember-test-helpers/wait';
import { waitForSockets } from '../helpers/wait-for-sockets';
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

skip('visiting /investigate-files/1234', function(assert) {
  assert.expect(1);

  const done = waitForSockets();

  visit('/investigate-files/1234');

  andThen(function() {
    assert.equal(currentURL(), '/investigate-files/1234', 'The route loads and we are not redirected');
  });

  andThen(function() {
    return wait().then(() => done());
  });
});

skip('visiting /investigate-files shows server down message', function(assert) {
  const request = lookup('service:request');
  sinon.stub(request, 'ping').callsFake(() => {
    return new rsvp.Promise((resolve, reject) => reject());
  });

  visit('/investigate-files');

  andThen(() => {
    assert.equal(currentURL(), '/investigate-files');
  });

  waitFor('.error-page');

  andThen(() => {
    assert.equal($('.error-page .title').text().trim(), 'Endpoint Server is offline');
  });
});
