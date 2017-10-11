import { getSocketDetails } from 'preferences/utils/preference-socket-provider';
import { module, test } from 'qunit';

module('Unit | Utility | preference socket provider');

test('Should return get socket details for requested preferencesFor', function(assert) {
  const result = getSocketDetails('events', 'get');
  assert.equal(result.modelName, 'investigate-preferences');
  assert.equal(result.method, 'getPreferences');
});

test('Should return set socket details for requested preferencesFor', function(assert) {
  const result = getSocketDetails('events', 'set');
  assert.equal(result.modelName, 'investigate-preferences');
  assert.equal(result.method, 'setPreferences');
});
