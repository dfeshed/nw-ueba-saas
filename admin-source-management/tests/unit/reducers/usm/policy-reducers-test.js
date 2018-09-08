import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';
import { module, /* test, */ skip } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/policy-reducers';

// ****************************************************************************
// skipping all tests as the create policy component is being replaced...
// we'll delete this once the new policy wizard is finished and is fully tested
// ****************************************************************************

module('Unit | Reducers | Policy Reducers');

const initialState = {
  policy: {
    name: '',
    description: '',
    scheduleConfig: {
      scanType: 'MANUAL',
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null,
        scanStartTime: '10:00',
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: []
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },
  policyStatus: null,
  availableSettings: [
    { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
    { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/effective-date' },
    { index: 2, id: 'recurrenceInterval', label: 'Scan Frequency', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/recurrence-interval' },
    { index: 3, id: 'startTime', label: 'Start Time', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/start-time' },
    { index: 4, id: 'cpuMax', label: 'CPU Maximum', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/cpu-max' },
    { index: 5, id: 'vmMax', label: 'Virtual Machine Maximum', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/vm-max' }
  ],
  selectedSettings: []
};

const policyData = {
  'id': 'policy_001',
  'name': 'Zebra 001',
  'description': 'Zebra 001 of policy policy_001'
};

const scanScheduleId = 'schedOrManScan';
const effectiveDateId = 'effectiveDate';

skip('on NEW_POLICY, state should be reset to the initial state', function(assert) {
  // the reducer copies initialState with a policy.scheduleConfig.scheduleOptions.scanStartDate of today
  const initialStateCopy = _.cloneDeep(initialState);
  initialStateCopy.policy.scheduleConfig.scheduleOptions.scanStartDate = moment().startOf('date').toDate().getTime();

  const modifiedState = {
    ...initialState,
    policy: { id: 'mod_001', name: 'name 001', description: 'desc 001' },
    policyStatus: 'complete'
  };
  const action = { type: ACTION_TYPES.NEW_POLICY };
  const endState = reducers(Immutable.from(modifiedState), action);
  assert.deepEqual(endState, initialStateCopy);
});

skip('on EDIT_POLICY, name & description are properly set', function(assert) {
  // edit name test
  const nameExpected = 'name 001';
  const nameExpectedEndState = {
    ...initialState,
    policy: { ...initialState.policy, name: nameExpected }
  };
  const nameAction = {
    type: ACTION_TYPES.EDIT_POLICY,
    payload: { field: 'policy.name', value: nameExpected }
  };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState, nameExpectedEndState, `policy name is ${nameExpected}`);
});

skip('on UPDATE_POLICY policy is updated', function(assert) {
  const payload = {
    scheduleConfig: {
      scheduleOptions: {
        recurrenceIntervalUnit: 'WEEKS'
      }
    }
  };

  const endState = 'WEEKS';
  const nameAction = { type: ACTION_TYPES.UPDATE_POLICY_PROPERTY, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState.policy.scheduleConfig.scheduleOptions.recurrenceIntervalUnit, endState, 'recurrenceIntervalUnit is updated along with recurrenceInterval');
});

skip('on SAVE_POLICY start, groupStatus is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    policyStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_POLICY });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policyStatus is wait');
});

skip('on SAVE_POLICY success, policy & policyStatus are properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    policy: policyData,
    policyStatus: 'complete'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SAVE_POLICY,
    payload: { data: policyData }
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy populated & policyStatus is complete');
});

skip('TOGGLE_SCAN_TYPE sets the scan type correctly', function(assert) {
  const payload = 'SCHEDULED';

  const endState = 'SCHEDULED';
  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState.policy.scheduleConfig.scanType, endState, 'scan type updated to SCHEDULED correctly');
});

skip('when MANUAL, TOGGLE_SCAN_TYPE clears out schedule and scan options', function(assert) {
  const payload = 'MANUAL';

  const endState = {
    scheduleOptions: null,
    scanOptions: null
  };
  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState.policy.scheduleConfig.scheduleOptions, endState.scheduleOptions, 'schedule options cleared out correctly');
  assert.deepEqual(nameEndState.policy.scheduleConfig.scanOptions, endState.scanOptions, 'scan options cleared out correctly');
});

skip('when MANUAL, TOGGLE_SCAN_TYPE greys out the effective date component in the available settings', function(assert) {
  const payload = 'MANUAL';

  const endState = {
    availableSettings: [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ]
  };
  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState.availableSettings[1].isGreyedOut, endState.availableSettings[1].isGreyedOut, 'Effective date component is greyed out correctly');
});

skip('when MANUAL, TOGGLE_SCAN_TYPE greys out the start-time component in the available settings', function(assert) {
  const payload = 'MANUAL';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, true, 'start-time component is greyed out correctly when MANUAL is selected');
});

skip('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the start-time component in the available settings', function(assert) {
  const payload = 'SCHEDULED';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, false, 'start-time component lights up correctly when SCHEDULED is selected');
});

skip('when MANUAL, TOGGLE_SCAN_TYPE greys out the scan frequency component in the available settings', function(assert) {
  const payload = 'MANUAL';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, true, 'scan frequency component is greyed out correctly when MANUAL is selected');
});

skip('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the scan frequency component in the available settings', function(assert) {
  const payload = 'SCHEDULED';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, false, 'scan frequency component lights up correctly when SCHEDULED is selected');
});

skip('when MANUAL, TOGGLE_SCAN_TYPE greys out the cpu maximum component in the available settings', function(assert) {
  const payload = 'MANUAL';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, true, 'cpu maximum component is greyed out correctly when MANUAL is selected');
});

skip('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the cpu maximum component in the available settings', function(assert) {
  const payload = 'SCHEDULED';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, false, 'cpu maximum component lights up correctly when SCHEDULED is selected');
});

skip('when MANUAL, TOGGLE_SCAN_TYPE greys out the virtual machine maximum component in the available settings', function(assert) {
  const payload = 'MANUAL';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, true, 'virtual machine maximum component is greyed out correctly when MANUAL is selected');
});

skip('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the virtual machine maximum component in the available settings', function(assert) {
  const payload = 'SCHEDULED';

  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.equal(nameEndState.availableSettings[2].isGreyedOut, false, 'virtual machine maximum component lights up correctly when SCHEDULED is selected');
});

skip('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the effective date component in available settings', function(assert) {
  const payload = 'SCHEDULED';

  const endState = {
    availableSettings: [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ]
  };
  const nameAction = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState.availableSettings[1].isGreyedOut, endState.availableSettings[1].isGreyedOut, 'Effective date component is greyed out correctly');
});

skip('ADD_TO_SELECTED_SETTINGS adds an entry to the selectedSettings array', function(assert) {
  const payload = 'schedOrManScan';
  const endState = {
    availableSettings: [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ],
    selectedSettings: [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' }
    ]
  };
  const nameAction = { type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState.selectedSettings.length, endState.selectedSettings.length, 'Entry added to Selected settings from Available Settings');
  assert.deepEqual(nameEndState.availableSettings[0].isEnabled, endState.availableSettings[0].isEnabled, 'isEnabled flag is changed toggled in availableSettings when it is added to selectedSettings');
});

skip('ADD_TO_SELECTED_SETTINGS adds an entry to the selectedSettings array and changes the isGreyed flag based on the scanType', function(assert) {
  const payload = 'schedOrManScan';
  const initialStateCopy = _.cloneDeep(initialState);
  initialStateCopy.policy.scheduleConfig.scanType = 'SCHEDULED';
  initialStateCopy.availableSettings = [
    { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
    { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
  ];

  const endState = {
    availableSettings: [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ],
    selectedSettings: [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' }
    ]
  };
  const nameAction = { type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, payload };
  const nameEndState = reducers(Immutable.from(initialStateCopy), nameAction);
  assert.deepEqual(nameEndState.availableSettings[1].isGreyedOut, endState.availableSettings[1].isGreyedOut, 'isGreyed flag is toggled correctly in availableSettings when it is added to selectedSettings');
});

skip('REMOVE_FROM_SELECTED_SETTINGS removes an entry from the selectedSettings array', function(assert) {
  const payload = effectiveDateId;
  const initialStateCopy = _.cloneDeep(initialState);

  initialStateCopy.selectedSettings = [
    { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
  ];
  const nameAction = { type: ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS, payload };
  const nameEndState = reducers(Immutable.from(initialStateCopy), nameAction);
  assert.deepEqual(nameEndState.selectedSettings.length, 0, 'The entry has been successfully removed from the selectedSettings array');
});

skip('RESET_SCAN_SCHEDULE_TO_DEFAULTS resets state to initial state when id is scanScheduleId', function(assert) {
  const payload = scanScheduleId;
  const initialStateCopy = _.cloneDeep(initialState);

  initialStateCopy.selectedSettings = [
    { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
    { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
  ];
  const nameAction = { type: ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS, payload };
  const nameEndState = reducers(Immutable.from(initialStateCopy), nameAction);
  assert.deepEqual(nameEndState.selectedSettings.length, 0, 'All other entries in selected settings are cleared out when id is scanScheduleId');
  assert.deepEqual(nameEndState, initialState, 'RESET_SCAN_SCHEDULE_TO_DEFAULTS should reset the state to the initial state');
});
