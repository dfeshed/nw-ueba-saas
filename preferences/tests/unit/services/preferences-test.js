import rsvp from 'rsvp';
import { moduleFor, test } from 'ember-qunit';

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

  const request = service.get('request');
  request.promiseRequest = () => {
    return new rsvp.Promise(function(resolve) {
      resolve({ defaultAnalysisView: 'text' });
    });
  };
  const done = assert.async();
  service.getPreferences('events').then((response) => {
    assert.ok(response, 'Expected promise callback to be invoked with a response.');
    assert.equal(response.defaultAnalysisView, 'text', 'Expected to return defaultAnalysisView as text.');
    done();
  });
});

/**
 * @private
 * @Test:: This will validate set preferences is setting valid preferences for requested preferenceFor.
 */
test('it can set preferences for given preference type', function(assert) {
  const service = this.subject();
  assert.ok(service);

  const request = service.get('request');
  request.promiseRequest = () => {
    return new rsvp.Promise(function(resolve) {
      resolve(true);
    });
  };
  const done = assert.async();
  service.setPreferences({ preferenceFor: 'events', data: { defaultAnalysisView: 'File' } }).then((response) => {
    assert.ok(response, 'Expected promise callback to be invoked with a response.');
    assert.equal(response, true, 'Expected to return true after persisting settings.');
    done();
  });
});

/**
 * @private
 * @Test:: This will validate getPreferences should throw proper error in case there is any issue while getting preferences from API.
 */
test('it should not get preferences for error conditions', function(assert) {
  const service = this.subject();
  assert.ok(service);

  const request = service.get('request');
  request.promiseRequest = () => {
    return new rsvp.Promise(function(resolve, reject) {
      reject({ errorCode: 500, errorText: 'Internal server error.' });
    });
  };
  const done = assert.async();
  service.getPreferences('events').then(() => {
    assert.fail('Server is throwing error this should not pass.');
    done();
  }).catch((errorObj) => {
    assert.ok(errorObj, 'Expected promise callback to be invoked with a response.');
    assert.equal(errorObj.errorCode, 500, 'Expected error from server.');
    assert.equal(errorObj.errorText, 'Internal server error.', 'Expected error from server.');
    done();
  });
});

/**
 * @private
 * @Test:: This will validate setPreferences should throw proper unauthorize error if user is not allowed to update settings.
 */
test('it should not be able to set preferences if not authorized', function(assert) {
  const service = this.subject();
  assert.ok(service);

  const request = service.get('request');
  request.promiseRequest = () => {
    return new rsvp.Promise(function(resolve, reject) {
      reject({ errorCode: 401, errorText: 'User is authorized to perform this operation.' });
    });
  };
  const done = assert.async();
  service.setPreferences({ preferenceFor: 'events', data: { defaultAnalysisView: 'File' } }).then(() => {
    assert.fail('Server is throwing error this should not pass.');
    done();
  }).catch((errorObj) => {
    assert.ok(errorObj, 'Expected promise callback to be invoked with a response.');
    assert.equal(errorObj.errorCode, 401, 'Expected error from server.');
    assert.equal(errorObj.errorText, 'User is authorized to perform this operation.', 'Expected unathorized error from server.');
    done();
  });
});
