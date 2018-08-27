import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import policyCreators from 'admin-source-management/actions/creators/policy-creators';
import ACTION_TYPES from 'admin-source-management/actions/types';

module('Unit | Actions | policy creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('addToSelectedSettings action creator returns proper type and payload', function(assert) {
    const action = policyCreators.addToSelectedSettings('id1');
    assert.equal(action.type, ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, 'action has the correct type');
    assert.deepEqual(action.payload, 'id1', 'payload has the correct ud');
  });

  test('removeFromSelectedSettings action creator returns proper type and payload', function(assert) {
    const action = policyCreators.removeFromSelectedSettings('id2');
    assert.equal(action.type, ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS, 'action has the correct type');
    assert.deepEqual(action.payload, 'id2', 'payload has the correct id');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is scanType', function(assert) {
    const action = policyCreators.updatePolicyProperty('scanType', 'foo');
    assert.equal(action.type, ACTION_TYPES.TOGGLE_SCAN_TYPE, 'action has the correct type');
    assert.deepEqual(action.payload, 'foo', 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is enabledScheduledScan', function(assert) {
    const action = policyCreators.updatePolicyProperty('enabledScheduledScan', true);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.enabledScheduledScan, true, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is cpuMaximum', function(assert) {
    const action = policyCreators.updatePolicyProperty('cpuMaximum', 50);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scanOptions.cpuMaximum, 50, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is cpuMaximumOnVirtualMachine', function(assert) {
    const action = policyCreators.updatePolicyProperty('cpuMaximumOnVirtualMachine', 50);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scanOptions.cpuMaximumOnVirtualMachine, 50, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is recurrenceIntervalUnit', function(assert) {
    const action = policyCreators.updatePolicyProperty('recurrenceIntervalUnit', 2);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scheduleOptions.recurrenceIntervalUnit, 2, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key does not match any if clauses', function(assert) {
    const action = policyCreators.updatePolicyProperty('bar', 2);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scheduleOptions, { bar: 2 }, 'payload has the correct value');
  });
});