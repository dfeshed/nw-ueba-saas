import { module, test } from 'qunit';

import { validateConfig } from 'investigate-shared/utils/validation-utils';

module('Unit | Utils | Validate Config util', function() {

  test('validateConfig when text is less than 3 chars long', function(assert) {
    const result = validateConfig({ password: 'te' });
    const expectedResult = {
      isPasswordError: true,
      passwordInvalidMessage: 'endpointRAR.errorMessages.invalidPasswordString'
    };
    assert.deepEqual(result, expectedResult);
  });

  test('validateConfig when empty string is passed', function(assert) {
    const result = validateConfig({ password: '' });
    const expectedResult = {
      isPasswordError: true,
      passwordInvalidMessage: 'endpointRAR.errorMessages.passwordEmptyMessage'
    };
    assert.deepEqual(result, expectedResult);
  });

  test('validateConfig when invalid string is passed', function(assert) {
    const result = validateConfig({ password: 't e' });
    const expectedResult = {
      isPasswordError: true,
      passwordInvalidMessage: 'endpointRAR.errorMessages.invalidPasswordString'
    };
    assert.deepEqual(result, expectedResult);
  });

  test('validateConfig when invalid server name is passed', function(assert) {
    const result = validateConfig({ address: 't e23423' });
    const expectedResult = {
      isServerError: true,
      invalidServerMessage: 'endpointRAR.errorMessages.invalidServer'
    };
    assert.deepEqual(result, expectedResult);
  });

  test('validateConfig when invalid port is passed', function(assert) {
    const result = validateConfig({ httpsPort: 'e23423' });
    const expectedResult = {
      isPortError: true,
      invalidPortMessage: 'endpointRAR.errorMessages.invalidPort'
    };
    assert.deepEqual(result, expectedResult);
  });

  test('validateConfig when port is passed which is not within range', function(assert) {
    const result1 = validateConfig({ httpsPort: 0 });
    const expectedResult = {
      isPortError: true,
      invalidPortMessage: 'endpointRAR.errorMessages.invalidPort'
    };
    assert.deepEqual(result1, expectedResult);

    const result2 = validateConfig({ httpsPort: 65536 });
    assert.deepEqual(result2, expectedResult);
  });

  test('validateConfig when invalid becon interval is passed', function(assert) {
    const result = validateConfig({ httpsBeaconIntervalInSeconds: '1.2' });
    const expectedResult = {
      isBeaconError: true,
      invalidBeaconIntervalMessage: 'endpointRAR.errorMessages.invalidBeaconInterval'
    };
    assert.deepEqual(result, expectedResult);
  });

  test('validateConfig when invalid hostname is passed', function(assert) {
    const result = validateConfig({ esh: 'e23423 fre' });
    const expectedResult = {
      isHostError: true,
      invalidHostNameMessage: 'endpointRAR.errorMessages.invalidHostName'
    };
    assert.deepEqual(result, expectedResult);
  });
});
