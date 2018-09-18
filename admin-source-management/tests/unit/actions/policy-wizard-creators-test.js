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

  test('removeFromSelectedSettings ac returns proper type when id is scanType', function(assert) {
    const scanScheduleId = 'scanType';
    const action = policyWizardCreators.removeFromSelectedSettings(scanScheduleId);
    assert.equal(action.type, ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS, 'action has the correct type');
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is scanType', function(assert) {
    const expectedAction = {
      type: ACTION_TYPES.TOGGLE_SCAN_TYPE,
      payload: 'MANUAL'
    };
    const action = policyWizardCreators.updatePolicyProperty('scanType', 'MANUAL');
    assert.deepEqual(action, expectedAction, 'action has correct type & payload');
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is recurrenceUnit', function(assert) {
    const expectedAction = {
      type: ACTION_TYPES.UPDATE_POLICY_PROPERTY,
      payload: [
        { field: 'policy.recurrenceUnit', value: 'DAYS' },
        { field: 'policy.recurrenceInterval', value: 1 },
        { field: 'policy.runOnDaysOfWeek', value: null }
      ]
    };
    const action = policyWizardCreators.updatePolicyProperty('recurrenceUnit', 'DAYS');
    assert.deepEqual(action, expectedAction, 'action has correct type & payload');
  });

  test('updatePolicyProperty action creator returns proper default type and payload for any other field', function(assert) {
    const expectedAction = {
      type: ACTION_TYPES.UPDATE_POLICY_PROPERTY,
      payload: [
        { field: 'policy.anyOtherField', value: 2 }
      ]
    };
    const action = policyWizardCreators.updatePolicyProperty('anyOtherField', 2);
    assert.deepEqual(action, expectedAction, 'action has correct type & payload');
  });
});