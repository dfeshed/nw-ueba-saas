import { module, test } from 'qunit';
import { isOSWindows, isModeAdvance, isAgentVersionAdvanced } from 'investigate-hosts/reducers/utils/mft-utils';

module('Unit | Selectors | overview');

test('isOSWindows when OS is correct', function(assert) {
  const result = isOSWindows('windows');
  assert.equal(result, true);
});

test('isOSWindows when OS is wrong', function(assert) {
  const result = isOSWindows('mac');
  assert.equal(result, false);
});

test('isModeAdvance when mode is correct', function(assert) {
  const result = isModeAdvance('advanced');
  assert.equal(result, true);
});

test('isModeAdvance when mode is wrong', function(assert) {
  const result = isModeAdvance('insights');
  assert.equal(result, false);
});

test('isAgentVersionAdvanced when mode is wrong', function(assert) {
  const result = isAgentVersionAdvanced('11.3.0');
  assert.equal(result, false);
});

test('isAgentVersionAdvanced when mode is correct', function(assert) {
  const result = isAgentVersionAdvanced('11.7.0');
  assert.equal(result, true);
});