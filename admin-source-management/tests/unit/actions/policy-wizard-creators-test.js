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
        assert.equal(action.type, ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS, 'action has the correct type');
      }
    };
    const thunk = policyWizardCreators.addToSelectedSettings('id1');
    thunk(dispatch);
  });

  test('removeFromSelectedSettings action creator returns proper type and payload', function(assert) {
    const dispatch = (action) => {
      // first dispatch has a payload
      if (action.payload) {
        assert.equal(action.type, ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS, 'action has the correct type');
        assert.deepEqual(action.payload, 'id1', 'payload has the correct id');
      } else {
        // second dispatch does not have a payload
        assert.equal(action.type, ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS, 'action has the correct type');
      }
    };
    const thunk = policyWizardCreators.removeFromSelectedSettings('id1');
    thunk(dispatch);
    //
    // const action = policyWizardCreators.removeFromSelectedSettings('id2');
    // assert.equal(action.type, ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS, 'action has the correct type');
    // assert.deepEqual(action.payload, 'id2', 'payload has the correct id');
  });

  test('removeFromSelectedSettings ac returns proper type when id is scanType', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS:
          assert.equal(action.type, ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS, 'action has the correct type');
          break;
        case ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS:
          assert.equal(action.type, ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS, 'action has the correct type');
          break;
        default:
          assert.equal(true, false, 'action has the correct type');
      }
    };
    const scanScheduleId = 'scanType';
    const thunk = policyWizardCreators.removeFromSelectedSettings(scanScheduleId);
    thunk(dispatch);
  });

  test('updatePolicyType action creator returns proper type(s), payload(s), and/or promise(s) when policyType is edrPolicy', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.UPDATE_POLICY_TYPE:
          assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_TYPE, 'action has the correct type of UPDATE_POLICY_TYPE');
          assert.equal(action.payload, 'edrPolicy', 'action has the correct payload of edrPolicy');
          break;
        case ACTION_TYPES.FETCH_ENDPOINT_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_ENDPOINT_SERVERS, 'action has the correct type of FETCH_ENDPOINT_SERVERS');
          assert.ok(action.promise, 'action has a fetchEndpointServers promise');
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const thunk = policyWizardCreators.updatePolicyType('edrPolicy');
    thunk(dispatch);
  });

  test('updatePolicyType action creator returns proper type(s), payload(s), and/or promise(s) when policyType is windowsLogPolicy', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.UPDATE_POLICY_TYPE:
          assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_TYPE, 'action has the correct type of UPDATE_POLICY_TYPE');
          assert.equal(action.payload, 'windowsLogPolicy', 'action has the correct payload of windowsLogPolicy');
          break;
        case ACTION_TYPES.FETCH_LOG_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_LOG_SERVERS, 'action has the correct type of FETCH_LOG_SERVERS');
          assert.ok(action.promise, 'action has a fetchLogpointServers promise');
          break;
        // case ACTION_TYPES.SOME_WIN_LOG_THING:
        //   assert.equal(action.type, ACTION_TYPES.SOME_WIN_LOG_THING, 'action has the correct type of SOME_WIN_LOG_THING');
        //   assert.ok(action.promise, 'action has a someWinLogThing promise');
        //   break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const thunk = policyWizardCreators.updatePolicyType('windowsLogPolicy');
    thunk(dispatch);
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is scanType', function(assert) {
    const expectedAction = {
      type: ACTION_TYPES.TOGGLE_SCAN_TYPE,
      payload: 'DISABLED'
    };
    const action = policyWizardCreators.updatePolicyProperty('scanType', 'DISABLED');
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

  test('updatePolicyProperty action creator returns proper type and payload when field is primaryAddress', function(assert) {
    const val = { id: 'id1', host: '10.10.10.10' };
    const expectedAction = {
      type: ACTION_TYPES.UPDATE_POLICY_PROPERTY,
      payload: [
        { field: 'policy.primaryNwServiceId', value: 'id1' },
        { field: 'policy.primaryAddress', value: '10.10.10.10' }
      ]
    };
    const action = policyWizardCreators.updatePolicyProperty('primaryAddress', val);
    assert.deepEqual(action, expectedAction, 'action has correct type & payload');
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is primaryDestination', function(assert) {
    const val = { id: 'id1', host: '10.10.10.10' };
    const expectedAction = {
      type: ACTION_TYPES.UPDATE_POLICY_PROPERTY,
      payload: [
        { field: 'policy.primaryDestination', value: '10.10.10.10' }
      ]
    };
    const action = policyWizardCreators.updatePolicyProperty('primaryDestination', val);
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