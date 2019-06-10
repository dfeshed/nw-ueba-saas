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

  test('initializePolicyType action creator returns proper type(s) and payload(s) when isDefaultPolicy is true', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.FETCH_ENDPOINT_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_ENDPOINT_SERVERS, 'action has the correct type');
          break;
        case ACTION_TYPES.EDR_DEFAULT_POLICY:
          assert.equal(action.type, ACTION_TYPES.EDR_DEFAULT_POLICY, 'action has the correct type');
          break;
        default:
          assert.equal(true, false, 'action has the correct type');
      }
    };
    const isDefaultPolicy = true;
    policyWizardCreators.initializePolicyType('edrPolicy', dispatch, isDefaultPolicy);
  });

  test('initializePolicyType action creator returns proper type(s) and payload(s) when isDefaultPolicy is false', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.FETCH_ENDPOINT_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_ENDPOINT_SERVERS, 'action has the correct type');
          break;
        default:
          assert.equal(true, false, 'action has the correct type');
      }
    };
    const isDefaultPolicy = false;
    policyWizardCreators.initializePolicyType('edrPolicy', dispatch, isDefaultPolicy);
  });

  test('initializePolicyType action creator returns proper type(s) and payload(s) when policy is windowsLogPolicy', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.FETCH_LOG_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_LOG_SERVERS, 'action has the correct type');
          break;
        default:
          assert.equal(true, false, 'action has the correct type');
      }
    };
    policyWizardCreators.initializePolicyType('windowsLogPolicy', dispatch);
  });

  test('initializePolicyType action creator returns proper type(s) and payload(s) when policy is filePolicy', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.FETCH_LOG_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_LOG_SERVERS, 'action has the correct type');
          break;
        case ACTION_TYPES.FETCH_FILE_SOURCE_TYPES:
          assert.equal(action.type, ACTION_TYPES.FETCH_FILE_SOURCE_TYPES, 'action has the correct type');
          break;
        default:
          assert.equal(true, false, 'action has the correct type');
      }
    };
    policyWizardCreators.initializePolicyType('filePolicy', dispatch);
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
    assert.expect(3);
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.TOGGLE_SCAN_TYPE:
          assert.equal(action.type, ACTION_TYPES.TOGGLE_SCAN_TYPE, 'action has the correct type of TOGGLE_SCAN_TYPE');
          assert.equal(action.payload, 'DISABLED', 'action has the correct payload of DISABLED');
          break;
        case ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS:
          assert.equal(action.type, ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS, 'action has the correct type of UPDATE_HEADERS_FOR_ALL_SETTINGS');
          // no payload for this action
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const thunk = policyWizardCreators.updatePolicyProperty('scanType', 'DISABLED');
    thunk(dispatch);
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is recurrenceUnit', function(assert) {
    assert.expect(2);
    const expectedPayload = [
      { field: 'policy.recurrenceUnit', value: 'DAYS' },
      { field: 'policy.recurrenceInterval', value: 1 },
      { field: 'policy.runOnDaysOfWeek', value: null }
    ];
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.UPDATE_POLICY_PROPERTY:
          assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type of UPDATE_POLICY_PROPERTY');
          assert.deepEqual(action.payload, expectedPayload, 'action has the correct payload');
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const thunk = policyWizardCreators.updatePolicyProperty('recurrenceUnit', 'DAYS');
    thunk(dispatch);
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is primaryAddress', function(assert) {
    assert.expect(2);
    const expectedPayload = [
      { field: 'policy.primaryNwServiceId', value: 'id1' },
      { field: 'policy.primaryAddress', value: '10.10.10.10' }
    ];
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.UPDATE_POLICY_PROPERTY:
          assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type of UPDATE_POLICY_PROPERTY');
          assert.deepEqual(action.payload, expectedPayload, 'action has the correct payload');
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const val = { id: 'id1', host: '10.10.10.10' };
    const thunk = policyWizardCreators.updatePolicyProperty('primaryAddress', val);
    thunk(dispatch);
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is primaryDestination', function(assert) {
    assert.expect(2);
    const expectedPayload = [
      { field: 'policy.primaryDestination', value: '10.10.10.10' }
    ];
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.UPDATE_POLICY_PROPERTY:
          assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type of UPDATE_POLICY_PROPERTY');
          assert.deepEqual(action.payload, expectedPayload, 'action has the correct payload');
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const val = { id: 'id1', host: '10.10.10.10' };
    const thunk = policyWizardCreators.updatePolicyProperty('primaryDestination', val);
    thunk(dispatch);
  });

  test('updatePolicyProperty action creator returns proper type and payload when field is selectedFileSource', function(assert) {
    assert.expect(2);
    const expectedPayload = [
      { field: 'policy.selectedFileSource', value: 'apache' }
    ];
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.UPDATE_POLICY_PROPERTY:
          assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type of UPDATE_POLICY_PROPERTY');
          assert.deepEqual(action.payload, expectedPayload, 'action has the correct payload');
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const val = { name: 'apache', prettyName: 'apache' };
    const thunk = policyWizardCreators.updatePolicyProperty('selectedFileSource', val);
    thunk(dispatch);
  });

  test('updatePolicyProperty action creator returns proper default type and payload for any other field', function(assert) {
    assert.expect(2);
    const expectedPayload = [
      { field: 'policy.anyOtherField', value: 2 }
    ];
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.UPDATE_POLICY_PROPERTY:
          assert.equal(action.type, ACTION_TYPES.UPDATE_POLICY_PROPERTY, 'action has the correct type of UPDATE_POLICY_PROPERTY');
          assert.deepEqual(action.payload, expectedPayload, 'action has the correct payload');
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };
    const thunk = policyWizardCreators.updatePolicyProperty('anyOtherField', 2);
    thunk(dispatch);
  });
});
