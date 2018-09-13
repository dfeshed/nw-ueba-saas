import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/policy-wizard-reducers';

const policyWizInitialState = new ReduxDataHelper().policyWiz().build().usm.policyWizard;
const scanScheduleId = 'schedOrManScan';
const effectiveDateId = 'effectiveDate';

module('Unit | Reducers | Policy Wizard Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, policyWizInitialState);
  });

  test('on NEW_POLICY, state should be reset to the initial state', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus('complete')
      .policyWizScanStartDate(moment().format('YYYY-MM-DD'))
      .build().usm.policyWizard;
    const action = { type: ACTION_TYPES.NEW_POLICY };
    const endState = reducers(Immutable.from(policyWizInitialState), action);
    assert.deepEqual(endState, expectedEndState, 'state reset to the initial state');
  });

  test('on EDIT_POLICY, name, description, etc. are properly set', function(assert) {
    // edit name test
    const nameExpected = 'test name';
    const nameExpectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(['policy.name'])
      .build().usm.policyWizard;
    const nameAction = {
      type: ACTION_TYPES.EDIT_POLICY,
      payload: { field: 'policy.name', value: nameExpected }
    };
    const nameEndState1 = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), nameAction);
    assert.deepEqual(nameEndState1, nameExpectedEndState, `policy name is ${nameExpected}`);
    const nameEndState2 = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), nameAction);
    assert.deepEqual(nameEndState2, nameExpectedEndState, `policy name is ${nameExpected} visited state contains no duplicates`);

    // edit description test
    const descExpected = 'test description';
    const descExpectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizDescription(descExpected)
      .policyWizVisited(['policy.description'])
      .build().usm.policyWizard;
    const descAction = {
      type: ACTION_TYPES.EDIT_POLICY,
      payload: { field: 'policy.description', value: descExpected }
    };
    const descEndState1 = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), descAction);
    assert.deepEqual(descEndState1, descExpectedEndState, `policy desc is ${descExpected}`);
    const descEndState2 = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), descAction);
    assert.deepEqual(descEndState2, descExpectedEndState, `policy desc is ${descExpected} visited state contains no duplicates`);
  });

  test('on GET_POLICY start, policy is reset and itemsStatus is properly set', function(assert) {
    const getPolicyEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus('wait')
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_POLICY });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, getPolicyEndState, 'policy is not-set and policyStatus is wait');
  });

  test('on GET_POLICY success, policy & itemsStatus are properly set', function(assert) {
    const getPolicyPayload = {
      data: [
        {
          'id': 'policy_001',
          'name': 'EMC 001',
          'description': 'EMC 001 of policy policy_001',
          'dirty': false
        }
      ]
    };

    const getPolicyEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicy(getPolicyPayload.data)
      .policyWizPolicyStatus('complete')
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_POLICY,
      payload: getPolicyPayload
    });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, getPolicyEndState, 'policy is not-set and policyStatus is complete');
  });

  test('on UPDATE_POLICY_PROPERTY policy is updated', function(assert) {
    const payload = {
      scheduleConfig: {
        scheduleOptions: {
          recurrenceIntervalUnit: 'WEEKS'
        }
      }
    };

    const recurrenceIntervalUnitExpected = 'WEEKS';
    const action = { type: ACTION_TYPES.UPDATE_POLICY_PROPERTY, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.policy.scheduleConfig.scheduleOptions.recurrenceIntervalUnit, recurrenceIntervalUnitExpected, 'recurrenceIntervalUnit is updated along with recurrenceInterval');
  });

  test('TOGGLE_SCAN_TYPE sets the scan type correctly', function(assert) {
    const payload = 'SCHEDULED';

    const scanTypeExpected = 'SCHEDULED';
    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.policy.scheduleConfig.scanType, scanTypeExpected, 'scan type updated to SCHEDULED correctly');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE clears out schedule and scan options', function(assert) {
    const payload = 'MANUAL';

    const scheduleConfigExpected = {
      scheduleOptions: null,
      scanOptions: null
    };
    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.policy.scheduleConfig.scheduleOptions, scheduleConfigExpected.scheduleOptions, 'schedule options cleared out correctly');
    assert.deepEqual(endState.policy.scheduleConfig.scanOptions, scheduleConfigExpected.scanOptions, 'scan options cleared out correctly');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE greys out the effective date component in the available settings', function(assert) {
    const payload = 'MANUAL';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.availableSettings[2].isGreyedOut, true, 'Effective date component is greyed out correctly');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE greys out the start-time component in the available settings', function(assert) {
    const payload = 'MANUAL';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[5].isGreyedOut, true, 'start-time component is greyed out correctly when MANUAL is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the start-time component in the available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[5].isGreyedOut, false, 'start-time component lights up correctly when SCHEDULED is selected');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE greys out the scan frequency component in the available settings', function(assert) {
    const payload = 'MANUAL';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[4].isGreyedOut, true, 'scan frequency component is greyed out correctly when MANUAL is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the scan frequency component in the available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[4].isGreyedOut, false, 'scan frequency component lights up correctly when SCHEDULED is selected');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE greys out the cpu maximum component in the available settings', function(assert) {
    const payload = 'MANUAL';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[7].isGreyedOut, true, 'cpu maximum component is greyed out correctly when MANUAL is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the cpu maximum component in the available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[7].isGreyedOut, false, 'cpu maximum component lights up correctly when SCHEDULED is selected');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE greys out the virtual machine maximum component in the available settings', function(assert) {
    const payload = 'MANUAL';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[8].isGreyedOut, true, 'virtual machine maximum component is greyed out correctly when MANUAL is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the virtual machine maximum component in the available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[8].isGreyedOut, false, 'virtual machine maximum component lights up correctly when SCHEDULED is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the effective date component in available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.availableSettings[2].isGreyedOut, false, 'Effective date component lights up correctly when SCHEDULED is selected');
  });

  test('ADD_TO_SELECTED_SETTINGS adds an entry to the selectedSettings array', function(assert) {
    const payload = 'schedOrManScan';
    const action = { type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.selectedSettings.length, 1, 'Entry added to Selected settings from Available Settings');
    assert.deepEqual(endState.availableSettings[1].isEnabled, false, 'isEnabled flag is changed toggled in availableSettings when it is added to selectedSettings');
  });

  test('ADD_TO_SELECTED_SETTINGS adds an entry to the selectedSettings array and changes the isGreyed flag based on the scanType', function(assert) {
    const payload = 'schedOrManScan';
    const initialStateCopy = _.cloneDeep(policyWizInitialState);
    initialStateCopy.policy.scheduleConfig.scanType = 'SCHEDULED';

    const action = { type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, payload };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(endState.availableSettings[1].isGreyedOut, false, 'isGreyed flag is toggled correctly in availableSettings when it is added to selectedSettings');
  });

  test('REMOVE_FROM_SELECTED_SETTINGS removes an entry from the selectedSettings array', function(assert) {
    const payload = effectiveDateId;
    const initialStateCopy = _.cloneDeep(policyWizInitialState);

    initialStateCopy.selectedSettings = [
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    const action = { type: ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS, payload };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(endState.selectedSettings.length, 0, 'The entry has been successfully removed from the selectedSettings array');
  });

  test('ADD_LABEL_TO_SELECTED_SETTINGS adds a label entry to the selectedSettings array', function(assert) {
    const initialStateCopy = _.cloneDeep(policyWizInitialState);
    initialStateCopy.selectedSettings = [
      { index: 1, id: 'schedOrManScan' }
    ];
    const action = { type: ACTION_TYPES.ADD_LABEL_TO_SELECTED_SETTINGS };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.equal(endState.selectedSettings.length, 2, 'An additional label entry has been added to the selectedSettings array');
  });

  test('RESET_SCAN_SCHEDULE_TO_DEFAULTS resets state to initial state when id is scanScheduleId', function(assert) {
    const payload = scanScheduleId;
    const initialStateCopy = _.cloneDeep(policyWizInitialState);

    initialStateCopy.selectedSettings = [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    const action = { type: ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS, payload };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(endState.selectedSettings.length, 0, 'All other entries in selected settings are cleared out when id is scanScheduleId');
    assert.deepEqual(endState, policyWizInitialState, 'RESET_SCAN_SCHEDULE_TO_DEFAULTS should reset the state to the initial state');
  });

  test('on SAVE_POLICY start, policyStatus is properly set', function(assert) {
    const policyStatusExpected = 'wait';
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus(policyStatusExpected)
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_POLICY });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `policyStatus is ${policyStatusExpected}`);
  });

  test('on SAVE_POLICY success, policy & policyStatus are properly set', function(assert) {
    const nameExpected = 'test name';
    const descExpected = 'test description';
    const policyStatusExpected = 'complete';
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizDescription(descExpected)
      .policyWizPolicyStatus(policyStatusExpected)
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_POLICY,
      payload: { data: _.cloneDeep(expectedEndState.policy) }
    });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `policy populated & policyStatus is ${policyStatusExpected}`);
  });

  test('on SAVE_PUBLISH_POLICY start, policyStatus is properly set', function(assert) {
    const policyStatusExpected = 'wait';
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus(policyStatusExpected)
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_PUBLISH_POLICY });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `policyStatus is ${policyStatusExpected}`);
  });

  test('on SAVE_PUBLISH_POLICY success, policy & policyStatus are properly set', function(assert) {
    const nameExpected = 'test name';
    const descExpected = 'test description';
    const policyStatusExpected = 'complete';
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizDescription(descExpected)
      .policyWizPolicyStatus(policyStatusExpected)
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_PUBLISH_POLICY,
      payload: { data: _.cloneDeep(expectedEndState.policy) }
    });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `policy populated & policyStatus is ${policyStatusExpected}`);
  });

});
