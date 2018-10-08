import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';
import { module, test, skip } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/policy-wizard-reducers';
import {
  endpointServers
} from '../../../data/data';

const policyWizInitialState = new ReduxDataHelper().policyWiz().build().usm.policyWizard;
const scanScheduleId = 'scanType';
const scanStartDateId = 'scanStartDate';

module('Unit | Reducers | Policy Wizard Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, policyWizInitialState);
  });

  test('on NEW_POLICY, state should be reset to the initial state', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus('complete')
      .policyWizScanStartDate(null)
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

  test('on FETCH_POLICY start, policy is reset and itemsStatus is properly set', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus('wait')
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_POLICY });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'policy is not-set and policyStatus is wait');
  });

  test('on FETCH_POLICY success, policy & itemsStatus are properly set', function(assert) {
    const fetchPolicyPayload = {
      data: {
        'id': 'policy_001',
        'name': 'EMC 001',
        'description': 'EMC 001 of policy policy_001',
        'dirty': false
      }
    };

    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicy(fetchPolicyPayload.data)
      .policyWizPolicyStatus('complete')
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_POLICY,
      payload: fetchPolicyPayload
    });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'policy is set and policyStatus is complete');
  });

  skip('on FETCH_POLICY success, availableSettings & selectedSettings are properly set', function(assert) {
    const initialStateCopy = _.cloneDeep(policyWizInitialState);
    const expectedEndState = {
      availableSettings: [
        { index: 0, id: 'scanScheduleHeader', label: 'adminUsm.policy.scanSchedule', isHeader: true, isEnabled: true, isGreyedOut: true },
        { index: 1, id: 'scanType', label: 'adminUsm.policy.schedOrManScan', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'scanType', value: 'MANUAL' }] },
        { index: 2, id: 'scanStartDate', label: 'adminUsm.policy.effectiveDate', isEnabled: true, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] },
        { index: 3, id: 'recurrenceInterval', label: 'adminUsm.policy.scanFrequency', isEnabled: false, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/recurrence-interval', defaults: [{ field: 'recurrenceInterval', value: 1 }, { field: 'recurrenceUnit', value: 'DAYS' }] },
        { index: 4, id: 'scanStartTime', label: 'adminUsm.policy.startTime', isEnabled: true, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/start-time', defaults: [{ field: 'scanStartTime', value: '10:00' }] },
        { index: 5, id: 'cpuMax', label: 'adminUsm.policy.cpuMax', isEnabled: false, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/cpu-max', defaults: [{ field: 'cpuMax', value: 75 }] },
        { index: 6, id: 'cpuMaxVm', label: 'adminUsm.policy.vmMaximum', isEnabled: false, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/vm-max', defaults: [{ field: 'cpuMaxVm', value: 85 }] },
        { index: 7, id: 'advScanSettingsHeader', label: 'adminUsm.policy.advScanSettings', isHeader: true, isEnabled: true },
        { index: 8, id: 'captureFloatingCode', label: 'adminUsm.policy.captureFloatingCode', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'captureFloatingCode', value: false }] },
        { index: 9, id: 'downloadMbr', label: 'adminUsm.policy.downloadMbr', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'downloadMbr', value: false }] },
        { index: 10, id: 'filterSignedHooks', label: 'adminUsm.policy.filterSignedHooks', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'filterSignedHooks', value: false }] },
        { index: 11, id: 'requestScanOnRegistration', label: 'adminUsm.policy.requestScanOnRegistration', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'requestScanOnRegistration', value: false }] },
        { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
        { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] },
        { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policy.endpointServerSettings', isHeader: true, isEnabled: true },
        { index: 15, id: 'primaryAddress', label: 'adminUsm.policy.primaryAddress', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/primary-address', defaults: [{ field: 'primaryAddress', value: '' }, { field: 'primaryNwServiceId', value: '' }] },
        { index: 16, id: 'primaryHttpPort', label: 'adminUsm.policy.primaryHttpPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryHttpPort', value: 443 }] },
        { index: 17, id: 'primaryUdpPort', label: 'adminUsm.policy.primaryUdpPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryUdpPort', value: 444 }] },
        { index: 18, id: 'agentSettingsHeader', label: 'adminUsm.policy.agentSettings', isHeader: true, isEnabled: true },
        { index: 19, id: 'agentMode', label: 'adminUsm.policy.agentMode', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'agentMode', value: 'NO_MONITORING' }] }
      ],
      selectedSettings: [
        { index: 1, id: 'scanType', label: 'adminUsm.policy.schedOrManScan', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'scanType', value: 'MANUAL' }] },
        { index: 3, id: 'recurrenceInterval', label: 'adminUsm.policy.scanFrequency', isEnabled: false, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/recurrence-interval', defaults: [{ field: 'recurrenceInterval', value: 1 }, { field: 'recurrenceUnit', value: 'DAYS' }] },
        { index: 5, id: 'cpuMax', label: 'adminUsm.policy.cpuMax', isEnabled: false, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/cpu-max', defaults: [{ field: 'cpuMax', value: 75 }] },
        { index: 6, id: 'cpuMaxVm', label: 'adminUsm.policy.vmMaximum', isEnabled: false, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/vm-max', defaults: [{ field: 'cpuMaxVm', value: 85 }] },
        { index: 8, id: 'captureFloatingCode', label: 'adminUsm.policy.captureFloatingCode', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'captureFloatingCode', value: false }] },
        { index: 9, id: 'downloadMbr', label: 'adminUsm.policy.downloadMbr', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'downloadMbr', value: false }] },
        { index: 10, id: 'filterSignedHooks', label: 'adminUsm.policy.filterSignedHooks', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'filterSignedHooks', value: false }] },
        { index: 11, id: 'requestScanOnRegistration', label: 'adminUsm.policy.requestScanOnRegistration', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'requestScanOnRegistration', value: false }] },
        { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] },
        { index: 15, id: 'primaryAddress', label: 'adminUsm.policy.primaryAddress', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/primary-address', defaults: [{ field: 'primaryAddress', value: '' }, { field: 'primaryNwServiceId', value: '' }] },
        { index: 19, id: 'agentMode', label: 'adminUsm.policy.agentMode', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'agentMode', value: 'NO_MONITORING' }] }
      ]
    };

    const fetchPolicyPayload = {
      data: {
        id: 'policy_014',
        policyType: 'edrPolicy',
        name: 'EMC Reston! 014',
        description: 'EMC Reston 014 of policy policy_014',
        scanType: 'SCHEDULED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        captureFloatingCode: true,
        downloadMbr: false,
        filterSignedHooks: false,
        requestScanOnRegistration: false,
        blockingEnabled: false,
        primaryAddress: '10.10.10.10',
        agentMode: false
      }
    };

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_POLICY,
      payload: fetchPolicyPayload
    });
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(endState.availableSettings, expectedEndState.availableSettings, 'availableSettings are properly set');
    assert.deepEqual(endState.selectedSettings, expectedEndState.selectedSettings, 'selectedSettings are properly set');
  });

  test('on FETCH_POLICY_LIST start, policyList is reset and policyListStatus is properly set', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyListStatus('wait')
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_POLICY_LIST });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'policyList is not-set and policyListStatus is wait');
  });

  test('on FETCH_POLICY_LIST success, policyList & policyListStatus are properly set', function(assert) {
    const fetchPolicyListPayload = {
      data: [
        {
          id: '__default_edr_policy',
          name: 'Default EDR Policy',
          policyType: 'edrPolicy',
          description: 'Default EDR Policy __default_edr_policy',
          lastPublishedOn: 1527489158739,
          dirty: false
        },
        {
          id: 'policy_001',
          name: 'EMC 001',
          policyType: 'edrPolicy',
          description: 'EMC 001 of policy policy_001',
          lastPublishedOn: 1527489158739,
          dirty: true
        },
        {
          id: 'policy_002',
          name: 'EMC Reston! 012',
          policyType: 'edrPolicy',
          description: 'EMC Reston 012 of policy policy_012',
          lastPublishedOn: 0,
          dirty: true
        }
      ]
    };

    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyList(fetchPolicyListPayload.data)
      .policyWizPolicyListStatus('complete')
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_POLICY_LIST,
      payload: fetchPolicyListPayload
    });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'policyList is not-set and policyListStatus is complete');
  });

  test('on UPDATE_POLICY_PROPERTY policy is updated', function(assert) {
    const payload = [
      { field: 'policy.recurrenceUnit', value: 'WEEKS' },
      { field: 'policy.recurrenceInterval', value: 1 }
    ];

    const recurrenceUnitExpected = 'WEEKS';
    const recurrenceIntervalExpected = 1;
    const action = { type: ACTION_TYPES.UPDATE_POLICY_PROPERTY, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.policy.recurrenceUnit, recurrenceUnitExpected, 'recurrenceUnit is updated');
    assert.deepEqual(endState.policy.recurrenceInterval, recurrenceIntervalExpected, 'recurrenceInterval is updated');
  });

  test('TOGGLE_SCAN_TYPE sets the scan type correctly', function(assert) {
    const payload = 'SCHEDULED';

    const scanTypeExpected = 'SCHEDULED';
    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.policy.scanType, scanTypeExpected, 'scan type updated to SCHEDULED correctly');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE clears out schedule and scan options', function(assert) {
    const currentState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanType('SCHEDULED')
      .policyWizScanStartDate('2018-09-13')
      .policyWizScanStartTime('10:00')
      .policyWizRecurrenceInterval(1)
      .policyWizRecurrenceUnit('DAYS')
      .policyWizRunOnDaysOfWeek(['TUESDAY'])
      .policyWizCpuMax(75)
      .policyWizCpuMaxVm(85)
      .build().usm.policyWizard;
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanType('MANUAL')
      .policyWizScanStartDate(null)
      .policyWizScanStartTime(null)
      .policyWizRecurrenceInterval(null)
      .policyWizRecurrenceUnit(null)
      .policyWizRunOnDaysOfWeek(null)
      .policyWizCpuMax(null)
      .policyWizCpuMaxVm(null)
      .build().usm.policyWizard;

    const payload = 'MANUAL';
    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(currentState)), action);
    assert.deepEqual(endState, expectedEndState, 'schedule and scan options cleared out correctly');
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
    assert.equal(endState.availableSettings[5].isGreyedOut, true, 'cpu maximum component is greyed out correctly when MANUAL is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the cpu maximum component in the available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[5].isGreyedOut, false, 'cpu maximum component lights up correctly when SCHEDULED is selected');
  });

  test('when MANUAL, TOGGLE_SCAN_TYPE greys out the virtual machine maximum component in the available settings', function(assert) {
    const payload = 'MANUAL';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[6].isGreyedOut, true, 'virtual machine maximum component is greyed out correctly when MANUAL is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the virtual machine maximum component in the available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.equal(endState.availableSettings[6].isGreyedOut, false, 'virtual machine maximum component lights up correctly when SCHEDULED is selected');
  });

  test('when SCHEDULED, TOGGLE_SCAN_TYPE lights up the effective date component in available settings', function(assert) {
    const payload = 'SCHEDULED';

    const action = { type: ACTION_TYPES.TOGGLE_SCAN_TYPE, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.availableSettings[2].isGreyedOut, false, 'Effective date component lights up correctly when SCHEDULED is selected');
  });

  test('ADD_TO_SELECTED_SETTINGS adds an entry to the selectedSettings array', function(assert) {
    const payload = 'scanType';
    const action = { type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, payload };
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState.selectedSettings.length, 1, 'Entry added to Selected settings from Available Settings');
    assert.deepEqual(endState.availableSettings[1].isEnabled, false, 'isEnabled flag is changed toggled in availableSettings when it is added to selectedSettings');
  });

  test('ADD_TO_SELECTED_SETTINGS adds an entry to the selectedSettings array and changes the isGreyed flag based on the scanType', function(assert) {
    const payload = 'scanType';
    const initialStateCopy = _.cloneDeep(policyWizInitialState);

    initialStateCopy.policy.scanType = 'SCHEDULED';
    initialStateCopy.availableSettings = [
      { index: 0, id: 'scanType', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/scan-schedule', defaults: [{ field: 'scanType', value: 'MANUAL' }] },
      { index: 1, id: 'scanStartDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] }
    ];

    const expectedEndState = {
      availableSettings: [
        { index: 0, id: 'scanType', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/scan-schedule', defaults: [{ field: 'scanType', value: 'MANUAL' }] },
        { index: 1, id: 'scanStartDate', label: 'Effective Date', isEnabled: true, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] }
      ],
      selectedSettings: [
        { index: 0, id: 'scanType', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/scan-schedule', defaults: [{ field: 'scanType', value: 'MANUAL' }] }
      ]
    };

    const action = { type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS, payload };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(endState.availableSettings[1].isGreyedOut, expectedEndState.availableSettings[1].isGreyedOut, 'isGreyed flag is toggled correctly in availableSettings when it is added to selectedSettings');
  });

  test('REMOVE_FROM_SELECTED_SETTINGS removes an entry from the selectedSettings array', function(assert) {
    const payload = scanStartDateId;
    const initialStateCopy = _.cloneDeep(policyWizInitialState);

    initialStateCopy.selectedSettings = [
      { index: 1, id: 'scanStartDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, component: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    const action = { type: ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS, payload };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(endState.selectedSettings.length, 0, 'The entry has been successfully removed from the selectedSettings array');
  });

  test('UPDATE_HEADERS_FOR_ALL_SETTINGS adds a label entry to the selectedSettings array', function(assert) {
    const initialStateCopy = _.cloneDeep(policyWizInitialState);
    initialStateCopy.selectedSettings = [
      { index: 1, id: 'scanType' }
    ];
    const action = { type: ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.equal(endState.selectedSettings.length, 2, 'An additional label entry has been added to the selectedSettings array');
  });

  test('RESET_SCAN_SCHEDULE_TO_DEFAULTS resets scan schedule state to initial state when id is scanScheduleId', function(assert) {
    const payload = scanScheduleId;
    const initialStateCopy = _.cloneDeep(policyWizInitialState);

    initialStateCopy.selectedSettings = [
      { index: 0, id: 'scanType', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, component: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'scanStartDate', label: 'adminUsm.policy.effectiveDate', isEnabled: false, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/effective-date' },
      { index: 10, id: 'captureFloatingCode', label: 'adminUsm.policy.captureFloatingCode', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios' }
    ];

    const expectedEndState = {
      selectedSettings: [
        { index: 10, id: 'captureFloatingCode', label: 'adminUsm.policy.captureFloatingCode', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios' }
      ]
    };

    const action = { type: ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS, payload };
    const endState = reducers(Immutable.from(initialStateCopy), action);

    assert.deepEqual(endState.selectedSettings, expectedEndState.selectedSettings, 'All entries related to scan schedule in selected settings are cleared out when id is scanScheduleId');
    assert.deepEqual(endState.availableSettings[0].isEnabled, true, 'RESET_SCAN_SCHEDULE_TO_DEFAULTS should move scan schedule to the left and set its defaults');
    assert.deepEqual(endState.availableSettings[1].isEnabled, true, 'RESET_SCAN_SCHEDULE_TO_DEFAULTS should move all the children of scan schedule to the left and set its defaults');
  });

  test('UPDATE_HEADERS_FOR_ALL_SETTINGS moves the header to the right correctly', function(assert) {
    const initialStateCopy = _.cloneDeep(policyWizInitialState);

    initialStateCopy.selectedSettings = [
      { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] }
    ];

    const expectedEndState = {
      selectedSettings: [
        { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
        { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] }
      ]
    };

    const action = { type: ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(_.sortBy(endState.selectedSettings, 'index'), expectedEndState.selectedSettings, 'Since blocking component exists on the right, the header for blocking is correctly moved to the right');
    assert.deepEqual(endState.availableSettings[12].isEnabled, false, 'Since blocking component is on the right, its header should not exist on the left');
  });

  test('UPDATE_HEADERS_FOR_ALL_SETTINGS moves the header to the right and also keeps it on the left', function(assert) {
    const initialStateCopy = _.cloneDeep(policyWizInitialState);
    initialStateCopy.selectedSettings = [
      { index: 15, id: 'primaryAddress', label: 'adminUsm.policy.primaryAddress', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/primary-address', defaults: [{ field: 'primaryAddress', value: '' }] },
      { index: 16, id: 'primaryHttpPort', label: 'adminUsm.policy.primaryHttpPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryHttpPort', value: 443 }] }
    ];

    const expectedEndState = {
      selectedSettings: [
        { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policy.endpointServerSettings', isHeader: true, isEnabled: true },
        { index: 15, id: 'primaryAddress', label: 'adminUsm.policy.primaryAddress', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/primary-address', defaults: [{ field: 'primaryAddress', value: '' }] },
        { index: 16, id: 'primaryHttpPort', label: 'adminUsm.policy.primaryHttpPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryHttpPort', value: 443 }] }
      ]
    };
    const action = { type: ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS };
    const endState = reducers(Immutable.from(initialStateCopy), action);
    assert.deepEqual(_.sortBy(endState.selectedSettings, 'index'), expectedEndState.selectedSettings, 'Since some endPointServer components exist on the right, the header exists on the right');
    assert.deepEqual(endState.availableSettings[14].isEnabled, true, 'Since atleast one component from endPointServer is still on the left, the header exists on the left');
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

  test('on FETCH_ENDPOINT_SERVERS start, endpoints list is reset', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServersEmpty()
      .build().usm.policyWizard;

    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ENDPOINT_SERVERS });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'list of endpoint servers is empty');
  });

  test('on FETCH_ENDPOINT_SERVERS success, endpoint servers list is properly set', function(assert) {
    const listOfEndpointServers = {
      data: endpointServers
    };

    const expectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .build().usm.policyWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_ENDPOINT_SERVERS,
      payload: listOfEndpointServers
    });
    const endState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'endpoint servers list is populated');
  });

});
