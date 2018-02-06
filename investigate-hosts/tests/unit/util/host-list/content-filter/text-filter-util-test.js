import { module, test } from 'qunit';
import { evaluateTextAgainstRegEx } from 'investigate-hosts/components/host-list/content-filter/text-filter/utils';


module('Unit | Util | Text filter');

// Agent ID
test('Agent ID Text filter util test', function(assert) {
  const validValue = [{ value: 'A62727B4-6DFB-4661-98D8-F4539AB4A4AE' }, { value: 'A62727B4-6DFB-4661-98D8-F4539AB4A' }];
  assert.equal(evaluateTextAgainstRegEx(validValue, 'agentID'), 0, 'Valid Agent ID');
});

test('Agent ID Text filter util test', function(assert) {
  const invalidValue = [{ value: '12AS@@##$$%%asdf' }, { value: '12AS@@##$asdf' }];
  assert.equal(evaluateTextAgainstRegEx(invalidValue, 'agentID'), 2, 'Invalid Agent ID');
});

// Agent Mode
test('Agent Mode Text filter util test', function(assert) {
  const validValue = [{ value: 'userModeOnly' }, { value: 'userModeOnlyTwo' }];
  assert.equal(evaluateTextAgainstRegEx(validValue, 'onlyAlphabetChars'), 0, 'Valid Agent Mode');
});

test('Agent Mode Text filter util test', function(assert) {
  const invalidValue = [{ value: '???mode@@only' }, { value: 'userModeOnly' }];
  assert.equal(evaluateTextAgainstRegEx(invalidValue, 'onlyAlphabetChars'), 1, 'Invalid Agent Mode');
});

// Agent Version
test('Agent Version Text filter util test', function(assert) {
  const validValue = [{ value: '4.4.0.2' }, { value: '11.1' }];
  assert.equal(evaluateTextAgainstRegEx(validValue, 'agentVersion'), 0, 'Valid Agent Version');
});

test('Agent Version Text filter util test', function(assert) {
  const invalidValue = [{ value: '4@4@4@' }, { value: '11.@1' }];
  assert.equal(evaluateTextAgainstRegEx(invalidValue, 'agentVersion'), 2, 'Invalid Agent Version');
});

// Ipv4
test('Ipv4 Text filter util test', function(assert) {
  const validValue = [{ value: '10.40.12.7' }, { value: '10.40.0.21' }];
  assert.equal(evaluateTextAgainstRegEx(validValue, 'ip'), 0, 'Valid Ipv4');
});

test('Ipv4 Text filter util test', function(assert) {
  const invalidValue = [{ value: '10.40.@.7.23' }, { value: 'userModeOnly' }];
  assert.equal(evaluateTextAgainstRegEx(invalidValue, 'ip'), 2, 'Invalid Ipv4');
});

// NIC Mac address
test('MAC address Text filter util test', function(assert) {
  const validValue = [{ value: '00:50:56:01:21:BB' }, { value: 'FF:FF:FF:FF:FF:FF' }];
  assert.equal(evaluateTextAgainstRegEx(validValue, 'macAddress'), 0, 'Valid MAC address');
});

test('MAC address Text filter util test', function(assert) {
  const invalidValue = [{ value: 'FF-FF-FF-FF-FF-FF' }, { value: '@@:@@:$$:#$:WE:R#' }];
  assert.equal(evaluateTextAgainstRegEx(invalidValue, 'macAddress'), 2, 'Invalid MAC address');
});
// OS Description
test('OS Description Text filter util test', function(assert) {
  const validValue = [{ value: 'CentOS Linux 7 (Core)' }, { value: 'CentOS Linux' }];
  assert.equal(evaluateTextAgainstRegEx(validValue, 'osDescription'), 0, 'Valid OS Description');
});

test('OS Description Text filter util test', function(assert) {
  const invalidValue = [{ value: 'wind@ws 7s@' }, { value: 'userModeOnly' }];
  assert.equal(evaluateTextAgainstRegEx(invalidValue, 'osDescription'), 1, 'Invalid OS Description');
});

