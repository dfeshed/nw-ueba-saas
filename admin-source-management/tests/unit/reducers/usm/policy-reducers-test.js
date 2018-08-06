import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/policy-reducers';

module('Unit | Reducers | Policy Reducers');

const initialState = {
  policy: {
    name: '',
    description: '',
    scheduleConfig: {
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
  policyStatus: null // wait, complete, error
};

const policyData = {
  'id': 'policy_001',
  'name': 'Zebra 001',
  'description': 'Zebra 001 of policy policy_001'
};

test('on NEW_POLICY, state should be reset to the initial state', function(assert) {
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

test('on EDIT_POLICY, name & description are properly set', function(assert) {
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

test('on UPDATE_POLICY policy is updated', function(assert) {
  const payload = {
    scheduleConfig: {
      scheduleOptions: {
        recurrenceIntervalUnit: 'WEEKS'
      }
    }
  };

  const endState = {
    policy: {
      name: '',
      description: '',
      scheduleConfig: {
        enabledScheduledScan: false,
        scheduleOptions: {
          scanStartDate: null,
          scanStartTime: '10:00',
          recurrenceInterval: 5,
          recurrenceIntervalUnit: 'WEEKS',
          runOnDaysOfWeek: []
        },
        scanOptions: {
          cpuMaximum: 75,
          cpuMaximumOnVirtualMachine: 85
        }
      }
    },
    policyStatus: null // wait, complete, error
  };
  const nameAction = { type: ACTION_TYPES.UPDATE_POLICY_PROPERTY, payload };
  const nameEndState = reducers(Immutable.from(initialState), nameAction);
  assert.deepEqual(nameEndState, endState, 'recurrenceIntervalUnit is updated along with recurrenceInterval');
});

test('on SAVE_POLICY start, groupStatus is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    policyStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_POLICY });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policyStatus is wait');
});

test('on SAVE_POLICY success, policy & policyStatus are properly set', function(assert) {
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