import Ember from 'ember';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import usersReducer from 'respond/reducers/respond/users';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';

const { copy } = Ember;

const initialState = {
  enabledUsers: [],
  enabledUsersStatus: null,
  allUsers: [],
  allUsersStatus: null
};

module('Unit | Utility | Users Actions - Reducers');

test('When FETCH_ALL_USERS starts, the allUsersStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const allUsersStatus = 'wait';
  const expectedEndState = {
    ...initState,
    allUsersStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_USERS });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS fails, the allUsersStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const allUsersStatus = 'error';
  const expectedEndState = {
    ...initState,
    allUsersStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ALL_USERS });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS succeeds, the allUsers array and allUsersStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const allUsers = [{ name: 'Ignatius Reilly' }];
  const allUsersStatus = 'completed';
  const expectedEndState = {
    ...initState,
    allUsersStatus,
    allUsers: [{ name: 'Ignatius Reilly', isInactive: false }]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_USERS, payload: { data: allUsers } });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS succeeds, the disabled property on any user gets remapped to isInactive', function(assert) {
  const initState = copy(initialState);
  const allUsers = [{ name: 'Ignatius Reilly', disabled: true }];
  const allUsersStatus = 'completed';
  const expectedEndState = {
    ...initState,
    allUsersStatus,
    allUsers: [{ name: 'Ignatius Reilly', isInactive: true }]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_USERS, payload: { data: allUsers } });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});


test('When FETCH_ALL_ENABLED_USERS starts, the enabledUsersStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const enabledUsersStatus = 'wait';
  const expectedEndState = {
    ...initState,
    enabledUsersStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_ENABLED_USERS fails, the enabledUsersStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const enabledUsersStatus = 'error';
  const expectedEndState = {
    ...initState,
    enabledUsersStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_ENABLED_USERS succeeds, the enabledUsers array and enabledUsersStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const enabledUsers = [{ name: 'Ignatius Reilly' }];
  const enabledUsersStatus = 'completed';
  const expectedEndState = {
    ...initState,
    enabledUsersStatus,
    enabledUsers: [{ name: 'Ignatius Reilly', isInactive: false }]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS, payload: { data: enabledUsers } });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

