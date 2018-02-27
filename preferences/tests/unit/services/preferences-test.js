import { moduleFor, test } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import rsvp from 'rsvp';
import sinon from 'sinon';
import { lookup } from 'ember-dependency-lookup';

moduleFor('service:preferences', 'Unit | Service | preferences', {
  // Specify the other units that are required for this test.
  needs: ['service:request'],
  beforeEach() {
    initialize(this);
  }
});

/**
 * @private
 * @Test:: This will validate get preferences is returning valid preferences for requested preferenceFor.
 */
test('it can get preferences for given preference type', function(assert) {
  const service = this.subject();
  assert.ok(service);
  const done = assert.async();
  service.getPreferences('investigate-events-preferences').then((response) => {
    assert.equal(response.eventAnalysisPreferences.currentReconView, 'PACKET', 'Expected to return defaultAnalysisView as packet.');
    done();
  });
});

/**
 * @private
 * @Test:: This will save preferences passed.
 */
test('it should be able to set preferences for given type', function(assert) {
  const service = this.subject();
  assert.ok(service);
  const done = assert.async();
  service.setPreferences('investigate-events-preferences', 'serviceId', { eventAnalysisPreferences: { currentReconView: 'FILE' } }).then((response) => {
    assert.equal(response.eventAnalysisPreferences.currentReconView, 'FILE', 'Expected to return defaultAnalysisView as text.');
    done();
  });
});

test('it should go to catch block of set preferences for given type', function(assert) {
  const request = lookup('service:request');
  const setPreferencesStub = sinon.stub(request, 'promiseRequest', () => {
    return new rsvp.Promise((resolve, reject) => reject());
  });
  const service = this.subject();
  const done = assert.async();
  service.setPreferences('investigate-events-preferences', 'serviceId', { eventAnalysisPreferences: { currentReconView: 'FILE' } }).then((response) => {
    assert.notOk(response, 'Response will not be defined now since the request is mocked to handle catch block');
    done();
    setPreferencesStub.restore();
  });
});