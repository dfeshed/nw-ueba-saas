import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import ACTION_TYPES from 'admin-source-management/actions/types';

module('Unit | Actions | policy wizard creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('addToSelectedSettings action creator returns proper type and payload', function(assert) {
    const dispatch = (action) => {
      // first dispatch has a payload
      if (action.payload) {
        assert.equal(action.type, ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, 'action has the correct type');
        assert.deepEqual(action.payload, 'id1', 'payload has the correct id');
      } else {
        // second dispatch does not have a payload
        assert.equal(action.type, ACTION_TYPES.ADD_LABEL_TO_SELECTED_SETTINGS, 'action has the correct type');
      }
    };
    const thunk = policyWizardCreators.addToSelectedSettings('id1');
    thunk(dispatch);
  });

  test('removeFromSelectedSettings action creator returns proper type and payload', function(assert) {
    const action = policyWizardCreators.removeFromSelectedSettings('id2');
    assert.equal(action.type, ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS, 'action has the correct type');
    assert.deepEqual(action.payload, 'id2', 'payload has the correct id');
  });

  test('removeFromSelectedSettings ac returns proper type when id is schedOrManScan', function(assert) {
    const scanScheduleId = 'schedOrManScan';
    const action = policyWizardCreators.removeFromSelectedSettings(scanScheduleId);
    assert.equal(action.type, ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS, 'action has the correct type');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is scanType', function(assert) {
    const action = policyWizardCreators.updatePolicyProperty('scanType', 'foo');
    assert.equal(action.type, ACTION_TYPES.TOGGLE_SCAN_TYPE, 'action has the correct type');
    assert.deepEqual(action.payload, 'foo', 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is enabledScheduledScan', function(assert) {
    const action = policyWizardCreators.updatePolicyProperty('enabledScheduledScan', true);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.enabledScheduledScan, true, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is cpuMaximum', function(assert) {
    const action = policyWizardCreators.updatePolicyProperty('cpuMaximum', 50);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scanOptions.cpuMaximum, 50, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is cpuMaximumOnVirtualMachine', function(assert) {
    const action = policyWizardCreators.updatePolicyProperty('cpuMaximumOnVirtualMachine', 50);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scanOptions.cpuMaximumOnVirtualMachine, 50, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key is recurrenceIntervalUnit', function(assert) {
    const action = policyWizardCreators.updatePolicyProperty('recurrenceIntervalUnit', 2);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scheduleOptions.recurrenceIntervalUnit, 2, 'payload has the correct value');
  });

  test('updatePolicyProperty action creator returns proper type and payload when key does not match any if clauses', function(assert) {
    const action = policyWizardCreators.updatePolicyProperty('bar', 2);
    assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type');
    assert.deepEqual(action.payload.scheduleConfig.scheduleOptions, { bar: 2 }, 'payload has the correct value');
  });
});