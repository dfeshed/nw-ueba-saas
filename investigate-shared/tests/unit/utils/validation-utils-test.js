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
});
