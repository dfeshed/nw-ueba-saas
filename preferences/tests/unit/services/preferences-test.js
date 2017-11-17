import { moduleFor, test } from 'ember-qunit';
import startApp from '../../helpers/start-app';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const application = startApp();
initialize(application);
moduleFor('service:preferences', 'Unit | Service | preferences', {
  // Specify the other units that are required for this test.
  needs: ['service:request']
});

/**
 * @private
 * @Test:: This will validate get preferences is returning valid preferences for requested preferenceFor.
 */
test('it can get preferences for given preference type', function(assert) {
  const service = this.subject();
  assert.ok(service);
  const done = assert.async();
  service.getPreferences('investigate-events').then((response) => {
    assert.equal(response.userServicePreferences.eventsPreferences.currentReconView, 'PACKET', 'Expected to return defaultAnalysisView as Packet.');
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
  service.setPreferences('investigate-events', 'serviceId', { userServicePreferences: { eventsPreferences: { currentReconView: 'FILE' } } }).then((response) => {
    assert.equal(response.userServicePreferences.eventsPreferences.currentReconView, 'FILE', 'Expected to return defaultAnalysisView as File.');
    done();
  });
});
