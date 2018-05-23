import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/policy/reducer';

module('Unit | Reducers | Policy Reducers');

const initialState = {
  policy: {
    name: '',
    description: ''
  },
  policyList: [],
  policyStatus: null,
  policySaveStatus: null // wait, complete, error
};

const policyData = {
  'id': 'policy_001',
  'name': 'Zebra 001',
  'description': 'Zebra 001 of policy policy_001'
};


test('on FETCH_POLICY_LIST start, policy is reset and policyStatus is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    policyStatus: 'wait',
    policyList: []
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_POLICY_LIST });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy is set and policyStatus is wait');
});

test('on FETCH_POLICY_LIST success, policy & policyStatus are properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    policyList: policyData,
    policyStatus: 'complete'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_POLICY_LIST,
    payload: { data: policyData }
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy list populated & policyStatus is complete');
});

test('should return the initial state', function(assert) {
  const endState = reducers(undefined, {});
  assert.deepEqual(endState, initialState);
});

test('on NEW_POLICY, state should be reset to the initial state', function(assert) {
  const modifiedState = {
    ...initialState,
    policy: { id: 'mod_001', name: 'name 001', description: 'desc 001' },
    policySaveStatus: 'complete'
  };
  const action = { type: ACTION_TYPES.NEW_POLICY };
  const endState = reducers(Immutable.from(modifiedState), action);
  assert.deepEqual(endState, initialState);
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

  // edit description test
  const descExpected = 'desc 001';
  const descExpectedEndState = {
    ...initialState,
    policy: { ...initialState.policy, description: descExpected }
  };
  const descAction = {
    type: ACTION_TYPES.EDIT_POLICY,
    payload: { field: 'policy.description', value: descExpected }
  };
  const descEndState = reducers(Immutable.from(initialState), descAction);
  assert.deepEqual(descEndState, descExpectedEndState, `policy description is ${descExpected}`);
});

test('on SAVE_POLICY start, groupSaveStatus is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    policySaveStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_POLICY });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policySaveStatus is wait');
});

test('on SAVE_POLICY success, policy & policySaveStatus are properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    policy: policyData,
    policySaveStatus: 'complete'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SAVE_POLICY,
    payload: { data: policyData }
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy populated & policySaveStatus is complete');
});
